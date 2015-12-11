package com.snack.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class DocumentSkill {
	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "document_id")
	private Document document;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "skill_id")
	private Skill skill;

	@Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
	private Boolean self;
}
