package com.snack.repository;

import com.snack.domain.Skill;
import com.snack.domain.User;
import com.snack.domain.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Integer> {
	UserSkill findByUserAndSkill(User managedUser, Skill managedSkill);
}