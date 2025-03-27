package com.yolo.backend.mvc.model.services;

import java.util.List;
import com.yolo.backend.mvc.model.entity.Alert;

public interface IAlertService {

	public List<Alert> findAll();

	public void save(Alert a);

	public Alert findById(String id);

	public void delete(Alert a);

	public Alert update(Alert a, String id);
}
