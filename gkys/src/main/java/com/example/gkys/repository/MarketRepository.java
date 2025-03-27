package com.example.gkys.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.gkys.model.MarketModel;

@Repository
public interface MarketRepository extends CrudRepository<MarketModel, Integer> {

}
