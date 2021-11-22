ALTER TABLE survey
    ADD CONSTRAINT checkStartBeforeEnd CHECK ( startDate < endDate );

CREATE TRIGGER checkDatesInFuture
    BEFORE INSERT
    ON survey
    FOR EACH ROW
BEGIN
    IF NEW.startDate <= CURDATE() OR NEW.endDate <= CURDATE() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Start- und/oder Enddatum in der Vergangenheit!';
    END IF;
END;

ALTER TABLE checkboxgroup
    ADD CONSTRAINT maxSelectGreaterThanEqualsMinSelect CHECK ( minSelect <= maxSelect ),
    ADD CONSTRAINT minSelectGreaterThanEqualsZero CHECK ( 0 <= minSelect ),
    ADD CONSTRAINT maxSelectGreaterThanEqualsTwo CHECK ( 2 <= maxSelect );