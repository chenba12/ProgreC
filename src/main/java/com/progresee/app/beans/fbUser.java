package com.progresee.app.beans;

import com.google.cloud.firestore.FieldValue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class fbUser {

	private String name;
	private String email;
	private String imageUrl;
	private FieldValue timestampFieldValue;
}
