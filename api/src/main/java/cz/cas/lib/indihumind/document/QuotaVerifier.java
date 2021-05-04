package cz.cas.lib.indihumind.document;

import cz.cas.lib.indihumind.card.CardStore;
import cz.cas.lib.indihumind.exception.UserQuotaReachedException;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static cz.cas.lib.indihumind.exception.UserQuotaReachedException.ErrorCode.USER_QUOTA_REACHED;

@Component
public class QuotaVerifier {

    private long userQuotaInKb;

    private UrlAttachmentFileStore urlDocumentStore;
    private LocalAttachmentFileStore localDocumentStore;
    private CardStore cardStore;
    private UserDelegate userDelegate;

    public void verify(long newAdditionalBytes) throws UserQuotaReachedException {
        long localDocsSizeKb = localDocumentStore.localAttachmentsSizeForUser(userDelegate.getId()) / 1000;
        long downloadedUrlDocsSizeKb = urlDocumentStore.urlAttachmentsSizeForUser(userDelegate.getId()) / 1000;
        long cardNoteSize = cardStore.cardNotesSizeForUser(userDelegate.getId()) / 1000;

        long sizeUserAlreadyHasKb = localDocsSizeKb + downloadedUrlDocsSizeKb + cardNoteSize;

        long newAdditionalSizeKb = newAdditionalBytes / 1000;
        if (sizeUserAlreadyHasKb + newAdditionalSizeKb > userQuotaInKb)
            throw new UserQuotaReachedException(USER_QUOTA_REACHED, userQuotaInKb);
    }


    @Inject
    public void setUrlDocumentStore(UrlAttachmentFileStore urlDocumentStore) {
        this.urlDocumentStore = urlDocumentStore;
    }

    @Inject
    public void setLocalDocumentStore(LocalAttachmentFileStore localDocumentStore) {
        this.localDocumentStore = localDocumentStore;
    }

    @Inject
    public void setCardStore(CardStore cardStore) {
        this.cardStore = cardStore;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }

    @Inject
    public void setUserQuotaInKb(@Value("${vzb.quota.kbPerUser}") Long userQuotaInKb) {
        this.userQuotaInKb = userQuotaInKb;
    }
}
