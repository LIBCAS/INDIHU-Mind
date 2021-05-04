package helper;

import core.exception.GeneralException;
import core.file.IndexedFileRef;
import core.index.IndexedDomainObject;
import core.notification.IndexedNotification;
import core.report.IndexedReport;
import cz.cas.lib.indihumind.card.IndexedCard;
import cz.cas.lib.indihumind.citation.IndexedCitation;
import cz.cas.lib.indihumind.citationtemplate.IndexedReferenceTemplate;
import cz.cas.lib.indihumind.document.IndexedAttachmentFile;
import cz.cas.lib.indihumind.security.user.IndexedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface AlterSolrCollection {
    Logger log = LoggerFactory.getLogger(AlterSolrCollection.class);

    String getCardTestCollectionName();

    String getUasTestCollectionName();

    /**
     * Retrieves indexed classes (Indexed-Entity) for altering {@link SolrDocument} from live collection to test
     * collection.
     *
     * @return set of classes which should have {@link SolrDocument#collection()} modified.
     */
    default Set<Class<? extends IndexedDomainObject>> getIndexedClassesForSolrAnnotationModification() {
        return Set.of(
                // UAS
                IndexedFileRef.class,
                IndexedNotification.class,
                IndexedReport.class,
                // INDIHU Mind
                IndexedCard.class,
                IndexedUser.class,
                IndexedCitation.class,
                IndexedReferenceTemplate.class,
                IndexedAttachmentFile.class
        );
    }


    /**
     * Changes value of {@link SolrDocument#collection()} for classes obtained by {@link
     * #getIndexedClassesForSolrAnnotationModification()}.
     *
     * Purpose of usage is to use test collection for tests (that can be wiped between tests) instead of live one (that
     * is used by application).
     *
     * @implNote Call this method from TestClass in {@code @Before} because this annotation does not work with
     *         interface's methods.
     */
    default void modifySolrDocumentAnnotationForIndexedClasses() {
        Set<Class<? extends IndexedDomainObject>> indexedClasses = new HashSet<>(getIndexedClassesForSolrAnnotationModification());
        indexedClasses.add(IndexedCard.class);

        for (Class<? extends IndexedDomainObject> clazz : indexedClasses) {
            if (IndexedCard.class.isAssignableFrom(clazz)) {
                modifySolrDocumentAnnotation(IndexedCard.class, getCardTestCollectionName());
            } else {
                modifySolrDocumentAnnotation(clazz, getUasTestCollectionName());
            }
        }
    }


    default void modifySolrDocumentAnnotation(Class<?> clazz, String testCollectionName) {
        SolrDocument solrDocAnnotation = clazz.getAnnotation(SolrDocument.class);
        if (solrDocAnnotation == null) {
            throw new IllegalArgumentException(String.format("Class:`%s` is missing @SolrDocument annotation. Verify you are using `Indexed-Entity` class in test initialization.", clazz.getSimpleName()));
        }

        try {
            modifyAnnotationValue(solrDocAnnotation, testCollectionName);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new GeneralException(String.format("Could not alter value of @SolrDocument.collection for class: `%s` at runtime. Error: %s", clazz.getSimpleName(), e.getMessage()));
        }
    }


    /**
     * Modifies value of annotation
     * <p>
     * If the implementation of <b>annotations</b> in Class.java changes, the code will break (i.e. it can break at any
     * time in the future) https://stackoverflow.com/a/28118436
     * </p>
     */
    @SuppressWarnings("unchecked")
    default void modifyAnnotationValue(Annotation annotation, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        final String ANNOTATION_METHOD_FOR_MODIFICATION = "collection";

        Object handler = Proxy.getInvocationHandler(annotation);
        Field javaField = handler.getClass().getDeclaredField("memberValues");
        javaField.setAccessible(true);

        Map<String, Object> memberValues = (Map<String, Object>) javaField.get(handler);
        Object oldValue = memberValues.get(ANNOTATION_METHOD_FOR_MODIFICATION);
        if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
            throw new IllegalArgumentException();
        }

        memberValues.put(ANNOTATION_METHOD_FOR_MODIFICATION, newValue);
        log.info(String.format("Annotation: [%s] was modified from: [%s] to new value: [%s]",
                annotation.annotationType().getTypeName(), oldValue.toString(), newValue.toString()));
    }

}
