INSERT INTO notification_category(name, version)
VALUES ('account', 0), ('vote', 0), ('survey', 0), ('misc', 0);

INSERT INTO notification_type(categoryId, name, suppressAnonymous, version)
SELECT c.id, n.name, FALSE, 0
FROM notification_category AS c
JOIN (
    VALUES ('vote/invite'),
           ('vote/participation'),
           ('vote/participation-invalidation')
) AS n(name) ON n.name LIKE (c.name || '/%');