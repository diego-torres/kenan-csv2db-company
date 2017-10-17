/**
 * 
 */
package com.nowgroup.kenan.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author dtorresf
 *
 */
public class CompanyNewItemProcessor implements ItemProcessor<Company, Company> {
	@Autowired
	private CompanyRepository companyRepo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.item.ItemProcessor#process(java.lang.Object)
	 */
	@Override
	public Company process(Company company) throws Exception {
		// Database is initialized with 2055 companies
		if(company.getId() < 2055)
			return null;
		if (!companyRepo.exists(company.getId()))
			return company;
		else
			return null;
	}

}
