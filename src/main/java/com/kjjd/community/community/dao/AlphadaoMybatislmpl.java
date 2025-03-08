package com.kjjd.community.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class AlphadaoMybatislmpl implements Alphadao{

	@Override
	public void select() {
		System.out.println("Mybatis");
	}
}
