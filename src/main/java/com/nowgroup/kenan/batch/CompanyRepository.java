/**
 * 
 */
package com.nowgroup.kenan.batch;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author dtorresf
 *
 */
@Repository
public interface CompanyRepository extends CrudRepository<Company, Integer> {
	
}
