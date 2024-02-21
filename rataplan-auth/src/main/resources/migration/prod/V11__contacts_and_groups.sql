CREATE TABLE contactGroup (
    id bigserial NOT NULL PRIMARY KEY,
    ownerId integer NOT NULL REFERENCES rataplanuser(id) ON DELETE CASCADE,
    name bytea NOT NULL,
    UNIQUE (ownerId, id),
    UNIQUE (ownerId, name)
);

CREATE TABLE contact (
    id bigserial NOT NULL,
    ownerId integer NOT NULL REFERENCES rataplanuser(id) ON DELETE CASCADE,
    userId integer NOT NULL REFERENCES rataplanuser(id) ON DELETE CASCADE,
    UNIQUE (ownerId, id),
    UNIQUE (ownerId, userId)
);

CREATE TABLE contactGroupContent (
    groupId bigint NOT NULL,
    groupOwner integer NOT NULL,
    contactId bigint NOT NULL,
    contactOwner integer NOT NULL,
    PRIMARY KEY (groupId, contactId),
    FOREIGN KEY (groupId, groupOwner) REFERENCES contactGroup(id, ownerId) ON DELETE CASCADE,
    FOREIGN KEY (contactId, contactOwner) REFERENCES contact(id, ownerId) ON DELETE CASCADE,
    CONSTRAINT same_owner CHECK (groupOwner = contactOwner)
);