package com.shybly.wikillm.util;

import com.shybly.wikillm.util.api_pojo.Project;
import com.shybly.wikillm.util.api_pojo.WikiPage;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.id.JdkSha256HexIdGenerator;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CronGitlabWikiIndexer {

    @Value("${gitlab.api.projects.url}")
    private String gitlabApiProjectsUrl;

    @Value("${gitlab.api.wiki.url}")
    private String gitlabApiWikiUrl;

    private final RestTemplate restTemplate;
    private final VectorStore vectorStore;

    public CronGitlabWikiIndexer(RestTemplate restTemplate, VectorStore vectorStore) {
        this.restTemplate = restTemplate;
        this.vectorStore = vectorStore;
    }

    @Scheduled(cron = "*/1 * * * * *")
    public void getProjectsWithWikiEnabled() {
        final Project[] projects = restTemplate.getForObject(gitlabApiProjectsUrl, Project[].class);

        final Set<Integer> projectIds = getProjectIds(projects);

        final List<WikiPage> wikiPages = new ArrayList<>();
        for (int projectId : projectIds) {
            final List<WikiPage> fetchedWikiPages = fetchAndProcessWikiPages(projectId);
            wikiPages.addAll(fetchedWikiPages);
        }

        final List<Document> documentsFromWikiPages = getDocumentsFromWikiPages(wikiPages);
        vectorStore.add(documentsFromWikiPages);
    }

    private static Set<Integer> getProjectIds(final Project[] projects) {
        if (projects == null) {
            throw new IllegalStateException("Expected projects response to not be null.");
        }

        final Set<Integer> projectIds = new HashSet<>();
        Project tmp;
        for (Project p : projects) {
            projectIds.add(p.getId());
            tmp = p;
            while (tmp.getForkedFromProject() != null && tmp.getForkedFromProject().getId() != null) {
                projectIds.add(tmp.getForkedFromProject().getId());
                tmp = tmp.getForkedFromProject();
            }
        }
        return projectIds;
    }

    private List<WikiPage> fetchAndProcessWikiPages(int projectId) {
        final String url = String.format(gitlabApiWikiUrl, projectId);

        final WikiPage[] wikiPages = restTemplate.getForObject(url, WikiPage[].class);

        if (wikiPages == null) {
            throw new IllegalStateException("Expected wiki pages response to not be null.");
        }

        return Arrays.stream(wikiPages).toList();
    }

    public List<Document> getDocumentsFromWikiPages(List<WikiPage> wikiPages) {
        final ArrayList<Document> result = new ArrayList<>();
        final JdkSha256HexIdGenerator jdkSha256HexIdGenerator = new JdkSha256HexIdGenerator();
        for (WikiPage wikiPage : wikiPages) {
            final Document document = new Document(
                    wikiPage.getContent(),
                    Map.of(
                            "title", wikiPage.getTitle(),
                            "slug", wikiPage.getSlug()
                    ),
                    jdkSha256HexIdGenerator
            );
            result.add(document);
        }
        return result;
    }
}
