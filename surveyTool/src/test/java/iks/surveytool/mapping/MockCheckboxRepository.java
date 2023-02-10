package iks.surveytool.mapping;

import iks.surveytool.entities.Checkbox;
import iks.surveytool.repositories.CheckboxRepository;

import java.util.stream.Stream;

public class MockCheckboxRepository extends MockRepository.MockAbstractEntityRepository<Checkbox> implements CheckboxRepository {
    public MockCheckboxRepository() {
        super(Checkbox::new);
    }
    
    @Override
    public Stream<Checkbox> findAllUnencrypted() {
        return this.findAll().stream().filter(c -> !c.getText().isEncrypted());
    }
}
