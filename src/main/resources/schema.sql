CREATE TABLE IF NOT EXISTS company (
  company_id int(11) NOT NULL AUTO_INCREMENT,
  company_name varchar(150) NOT NULL,
  company_alias varchar(150),
  PRIMARY KEY (company_id)
);

CREATE TABLE IF NOT EXISTS company_role (
  company_role_id int(11) NOT NULL AUTO_INCREMENT,
  company_role_name varchar(50) NOT NULL,
  PRIMARY KEY (company_role_id),
  UNIQUE KEY uni_company_role_name (company_role_name)
);

CREATE TABLE IF NOT EXISTS cross_company_role (
  company_id int(11),
  company_role_id int(11),
  UNIQUE KEY uni_company_role (company_id, company_role_id)
);
