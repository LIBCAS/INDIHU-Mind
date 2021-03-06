package core.security.authorization.assign.audit;

import core.audit.AuditEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
public class EntityRestoreEvent extends AuditEvent implements Serializable {
    private String userId;
    private String entityName;
    private String entityId;


    public EntityRestoreEvent(Instant created, String userId, String entityName, String entityId) {
        super(created, "ENTITY_RESTORE");
        this.userId = userId;
        this.entityName = entityName;
        this.entityId = entityId;
    }
}
