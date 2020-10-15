package cz.cas.lib.vzb.search.service;

import core.exception.BadArgument;
import core.exception.GeneralException;
import core.util.Utils.Pair;
import cz.cas.lib.vzb.search.searchable.AdvancedSearchClass;
import org.reflections.Reflections;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static core.exception.BadArgument.ErrorCode.UNEXPECTED_ARGUMENT;

@Service
public class AdvancedSearchLocator {

    // classes annotated with @SolrDocument  { simpleName : (class, solrCollectionName) }
    public final static Map<String, Pair<Class<? extends AdvancedSearchClass>, String>> SOLR_DOCUMENT_CLASSES = new HashMap<>();

    private final Reflections REFLECTIONS = new Reflections("cz.cas.lib.vzb");

    public static String getCollectionNameFor(Class<? extends AdvancedSearchClass> clazz) {
        String simpleName = clazz.getSimpleName();
        return SOLR_DOCUMENT_CLASSES.get(simpleName).getR();
    }


    public static <U extends AdvancedSearchClass> Class<U> getClassFromName(String name) {
        Pair<Class<? extends AdvancedSearchClass>, String> classWithCollectionName = SOLR_DOCUMENT_CLASSES.get(name);
        if (classWithCollectionName == null) {
            throw new BadArgument(UNEXPECTED_ARGUMENT, "There is no AdvancedSearch class mapping for such name: " + name);
        }

        @SuppressWarnings("unchecked")
        Class<U> searchClass = (Class<U>) classWithCollectionName.getL();

        return searchClass;
    }


    public static String getNameFromClass(Class<? extends AdvancedSearchClass> clazz) {
        return clazz.getSimpleName();
    }


    @PostConstruct
    private void initializeSearchClassMap() {
        Set<Class<? extends AdvancedSearchClass>> classForAdvancedSearch = REFLECTIONS.getSubTypesOf(AdvancedSearchClass.class);

        for (Class<? extends AdvancedSearchClass> clazz : classForAdvancedSearch) {
            SolrDocument solrDocumentAnnotation = clazz.getAnnotation(SolrDocument.class);
            if (solrDocumentAnnotation == null || solrDocumentAnnotation.collection().isEmpty())
                throw new GeneralException("Missing Solr @SolrDocument.collection for " + clazz.getSimpleName());

            String collectionName = solrDocumentAnnotation.collection();
            Pair<Class<? extends AdvancedSearchClass>, String> pair = new Pair<>(clazz, collectionName);
            SOLR_DOCUMENT_CLASSES.put(clazz.getSimpleName(), pair);
        }
    }

}