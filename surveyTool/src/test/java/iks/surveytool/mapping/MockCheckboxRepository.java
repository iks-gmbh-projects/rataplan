package iks.surveytool.mapping;

import iks.surveytool.entities.Checkbox;
import iks.surveytool.repositories.CheckboxRepository;

public class MockCheckboxRepository extends MockRepository.MockAbstractEntityRepository<Checkbox> implements CheckboxRepository {
    public MockCheckboxRepository() {
        super(Checkbox::new);
    }
}
