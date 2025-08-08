package gender.controller;

import auth.repository.internal.InternalGenderRepository;
import common.domain.entity.Gender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class GenderInitializer {

    @Inject
    InternalGenderRepository genderRepository;

    @Transactional
    public void init() {
        insertIfMissing("male");
        insertIfMissing("female");
        insertIfMissing("other");

        var genders = genderRepository.findByDetail("male");
        if (genders.isLeft() || genders.getRight().isEmpty()) {
            throw new RuntimeException("Genders table empty or data missing!");
        }
    }

    private void insertIfMissing(String detail) {
        var existing = genderRepository.findByDetail(detail);
        if (existing.isRight() && existing.getRight().isEmpty()) {
            Gender gender = new Gender();
            gender.setDetail(detail);
            genderRepository.createGender(gender);
        }
    }
}

