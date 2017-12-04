package com.services;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.xml.bind.JAXBElement;

import com.pojo.Animal;

public class AnimalResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	String id;

	AnimalService animalService;

	public AnimalResource(UriInfo uriInfo, Request request, String id) {
		this.uriInfo = uriInfo;
		this.request = request;
		this.id = id;
		animalService = new AnimalService();
	}

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Animal getAnimal() {
		Animal animal = animalService.getAnimal(id);
		return animal;
	}

	@GET
	@Produces(MediaType.TEXT_XML)
	public Animal getAnimalAsHtml() {
		Animal animal = animalService.getAnimal(id);
		return animal;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response putAnimal(JAXBElement<Animal> animalElement) {
		Animal animal = animalElement.getValue();
		Response response;
		if (animalService.getAnimals().containsKey(animal.getId())) {
			response = Response.noContent().build();
		} else {
			response = Response.created(uriInfo.getAbsolutePath()).build();
		}
		animalService.createAnimal(animal);
		return response;
	}

	@DELETE
	public void deleteAnimal() {
		animalService.deleteAnimal(id);
	}

}
