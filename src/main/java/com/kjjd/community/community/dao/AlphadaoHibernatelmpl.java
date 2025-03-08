package com.kjjd.community.community.dao;

import org.springframework.stereotype.Repository;

@Repository("Hibernatel")
public class AlphadaoHibernatelmpl implements Alphadao{

	@Override
	public void select() {
		System.out.println("Hibernate");
	}
}
