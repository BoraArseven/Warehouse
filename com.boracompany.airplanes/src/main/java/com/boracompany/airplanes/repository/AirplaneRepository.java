package com.boracompany.airplanes.repository;

import java.util.List;

import com.boracompany.airplanes.model.Airplane;

public interface AirplaneRepository {
    public List<Airplane> findAll();

    public Airplane findById(String id);

    public void save(Airplane airplane);

    public void delete(String id);

}
