CREATE TABLE contactGroup
(
    id      bigserial NOT NULL PRIMARY KEY,
    ownerId integer   NOT NULL REFERENCES rataplanuser (id) ON DELETE CASCADE,
    name    bytea     NOT NULL,
    UNIQUE (ownerId, id),
    UNIQUE (ownerId, name)
);

CREATE TABLE contact
(
    id      bigserial NOT NULL PRIMARY KEY,
    ownerId integer   NOT NULL REFERENCES rataplanuser (id) ON DELETE CASCADE,
    userId  integer   NOT NULL REFERENCES rataplanuser (id) ON DELETE CASCADE,
    UNIQUE (ownerId, userId)
);

CREATE TABLE contactGroupContent
(
    groupId        bigint  NOT NULL,
    groupOwnerId   integer NOT NULL,
    contactId      integer NOT NULL,
    contactOwnerId integer NOT NULL,
    PRIMARY KEY (groupId, contactId),
    FOREIGN KEY (groupId, groupOwnerId) REFERENCES contactGroup (id, ownerId) ON DELETE CASCADE,
    FOREIGN KEY (contactId, contactOwnerId) REFERENCES contact (userId, ownerId) ON DELETE CASCADE,
    CONSTRAINT same_owner CHECK (groupOwnerId = contactOwnerId)
);