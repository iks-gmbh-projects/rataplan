CREATE TABLE contactGroup (
    id bigserial NOT NULL PRIMARY KEY,
    ownerId integer NOT NULL REFERENCES rataplanuser(id) ON DELETE CASCADE,
    name bytea NOT NULL,
    UNIQUE (ownerId, id),
    UNIQUE (ownerId, name)
);

CREATE TABLE contact (
    ownerId integer NOT NULL REFERENCES rataplanuser(id) ON DELETE CASCADE,
    userId integer NOT NULL REFERENCES rataplanuser(id) ON DELETE CASCADE,
    PRIMARY KEY (ownerId, userId)
);

CREATE TABLE contactGroupContent (
    groupId bigint NOT NULL,
    groupOwner integer NOT NULL,
    contactId integer NOT NULL,
    contactOwner integer NOT NULL,
    PRIMARY KEY (groupId, contactId),
    FOREIGN KEY (groupId, groupOwner) REFERENCES contactGroup(id, ownerId) ON DELETE CASCADE,
    FOREIGN KEY (contactId, contactOwner) REFERENCES contact(userId, ownerId) ON DELETE CASCADE,
    CONSTRAINT same_owner CHECK (groupOwner = contactOwner)
);