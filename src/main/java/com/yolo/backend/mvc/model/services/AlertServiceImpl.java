package com.yolo.backend.mvc.model.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yolo.backend.mvc.model.dao.IAlertDAO;
import com.yolo.backend.mvc.model.entity.Alert;
import com.yolo.backend.mvc.model.exceptions.AlertNotFoundException;

@Service
public class AlertServiceImpl implements IAlertService {

	@Autowired
	private IAlertDAO alertDAO;
	
	@Override
	public List <Alert> findAll(){
		return (List<Alert>)alertDAO.findAll();
	}

	@Override
	public void save(Alert a) {
		alertDAO.save(a);
	}

	@Override
	public Alert findById(String id) {
		return alertDAO.findById(id)
				.orElseThrow(() -> new AlertNotFoundException(id));
	}

	@Override
	public void delete(Alert a) {
		alertDAO.delete(a);
	}

	@Override
	public Alert update(Alert a, String id) {
		Alert currentAlert = this.findById(id);
		currentAlert.setCreatedAt(a.getCreatedAt());
		currentAlert.setCryptoId(a.getCryptoId());
		currentAlert.setTargetPrice(a.getTargetPrice());
		currentAlert.setTriggered(a.isTriggered());
		currentAlert.setUserId(a.getUserId());
		this.save(currentAlert);
		return currentAlert;
	}
}
