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
public class CompanyExistingItemProcessor implements ItemProcessor<Company, Company> {
	@Autowired
	private CompanyRepository companyRepo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.item.ItemProcessor#process(java.lang.Object)
	 */
	@Override
	public Company process(Company company) throws Exception {
		Company dbCompany = null;
		if(companyRepo.exists(company.getId()))
			dbCompany = companyRepo.findOne(company.getId());
		
		if (dbCompany != null && !dbCompany.md5HashCode().equals(company.md5HashCode()))
			return company;
		else
			return null;
	}

}
