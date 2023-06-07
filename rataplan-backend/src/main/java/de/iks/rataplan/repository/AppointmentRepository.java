package de.iks.rataplan.repository;

import de.iks.rataplan.domain.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.stream.Stream;

public interface AppointmentRepository extends JpaRepository<VoteOption, Integer> {
    @Query(value = "SELECT * FROM appointment" +
        " WHERE (description IS NOT NULL AND description NOT LIKE 'ENC\\_\\_##\\_\\_%')" +
        " OR (url IS NOT NULL AND url NOT LIKE 'ENC\\_\\_##\\_\\_%')", nativeQuery = true)
    public Stream<VoteOption> findUnencrypted();
}
