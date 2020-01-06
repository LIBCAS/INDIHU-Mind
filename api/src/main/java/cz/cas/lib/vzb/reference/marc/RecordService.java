package cz.cas.lib.vzb.reference.marc;

import core.exception.ForbiddenObject;
import core.index.dto.Params;
import core.index.dto.Result;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.CardStore;
import cz.cas.lib.vzb.exception.NameAlreadyExistsException;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.util.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static core.util.Utils.eq;
import static core.util.Utils.isNull;
import static cz.cas.lib.vzb.util.OperationType.CREATE;
import static cz.cas.lib.vzb.util.OperationType.UPDATE;

@Service
@Slf4j
public class RecordService {

    private RecordStore store;
    private UserDelegate userDelegate;
    private CardStore cardStore;


    public void delete(Record entity) {
        List<Card> cardsOfRecord = cardStore.findCardsOfRecord(entity);
        log.debug(String.format("Deleting record %s of user %s and removing it from cards=[%s]",
                entity.getName(), entity.getOwner(), cardsOfRecord.stream().map(Card::getId).collect(Collectors.joining(", "))));

        cardsOfRecord.forEach(c -> {
            c.getRecords().remove(entity);
        });
        cardStore.save(cardsOfRecord);
        store.delete(entity);
    }


    public Record find(String id) {
        return store.find(id);
    }


    public Collection<Record> findByUser(String id) {
        return store.findByUser(id);
    }


    /**
     * Creates or updates {@link Record}
     * If record with provided ID in DTO is found then its update and owner of record is verified.
     * All DTO attributes are assigned to Record and this record is then persisted to DB.
     * <p>
     * LinkedCards have to be persisted with {@link CardStore} because {@link Card} is the owning entity
     * and Hibernate will only check that side when maintaining the association.
     * https://stackoverflow.com/questions/36239030/manytomany-relation-not-save
     *
     * @param dto with new attributes
     * @return Record with attributes from DTO
     */
    public Record save(SaveRecordDto dto) {
        Record newRecord = store.find(dto.getId());
        OperationType operation;

        if (newRecord != null) {
            operation = UPDATE;
            eq(newRecord.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(Record.class, dto.getId()));
            log.debug(String.format("Updating Record %s of user %s", dto.getId(), newRecord.getOwner()));
        } else {
            operation = CREATE;
            newRecord = new Record();
            log.debug(String.format("Creating new Record %s for user %s", dto.getId(), userDelegate.getUser()));
        }

        newRecord.setId(dto.getId());
        newRecord.setName(dto.getName());
        newRecord.setDataFields(dto.getDataFields());
        newRecord.setLeader(dto.getLeader());
        newRecord.setOwner(userDelegate.getUser());

        // enforce addUniqueConstraint `vzb_record_of_user_uniqueness` of columnNames="name,owner_id"
        Record nameExists = store.findEqualNameDifferentId(newRecord);
        isNull(nameExists, () -> new NameAlreadyExistsException(Record.class, nameExists.getId(), nameExists.getName()));

        List<Card> linkingCards = cardStore.findAllInList(dto.getLinkedCards());
        newRecord.setLinkedCards(new HashSet<>(linkingCards));

        store.save(newRecord);

        // remove this record from cards and set it anew later because linkedCards can be different in updated version
        if (operation == UPDATE) {
            List<Card> cardsOfRecord = cardStore.findCardsOfRecord(newRecord);
            for (Card c : cardsOfRecord) {
                c.getRecords().remove(newRecord);
                cardStore.save(c);
            }
        }

        for (Card c : linkingCards) {
            c.addRecord(newRecord);
            cardStore.save(c);
        }

        return newRecord;
    }


    public Result<Record> findAll(Params params) {
        return store.findAll(params);
    }


    @Inject
    public void setCardStore(CardStore cardStore) {
        this.cardStore = cardStore;
    }

    @Inject
    public void setStore(RecordStore store) {
        this.store = store;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }
}
