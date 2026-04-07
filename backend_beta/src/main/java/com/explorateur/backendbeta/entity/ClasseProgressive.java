package com.explorateur.backendbeta.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "classe_progressive")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClasseProgressive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
