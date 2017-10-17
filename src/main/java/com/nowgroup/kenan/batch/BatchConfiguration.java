/**
 * 
 */
package com.nowgroup.kenan.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * @author dtorresf
 *
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	// "file:/tmp/import/products-*"
	@Value("${csv.resource}")
	private Resource[] csvResource;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private DataSource dataSource;

	public MultiResourceItemReader<Company> multiResourceItemReader() {
		MultiResourceItemReader<Company> mri = new MultiResourceItemReader<>();
		mri.setResources(csvResource);
		mri.setDelegate(reader());
		return mri;
	}
	
	@Bean
	public FlatFileItemReader<Company> reader() {
		FlatFileItemReader<Company> reader = new FlatFileItemReader<>();
		//reader.setLinesToSkip(1);		
		reader.setLineMapper(new DefaultLineMapper<Company>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "id", "alias", "name" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<Company>() {
					{
						setTargetType(Company.class);
					}
				});
			}
		});
		return reader;
	}

	@Bean
	public CompanyNewItemProcessor companyNewItemProcessor() {
		return new CompanyNewItemProcessor();
	}

	@Bean
	public CompanyExistingItemProcessor companyExistingItemProcessor() {
		return new CompanyExistingItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Company> inserter() {
		JdbcBatchItemWriter<Company> writer = new JdbcBatchItemWriter<>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Company>());
		writer.setSql("INSERT INTO company (company_id, company_name, company_alias) VALUES (:id, :name, :alias)");
		writer.setDataSource(dataSource);
		return writer;
	}

	@Bean
	public JdbcBatchItemWriter<Company> updater() {
		JdbcBatchItemWriter<Company> writer = new JdbcBatchItemWriter<>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Company>());
		writer.setSql("UPDATE company SET company_name=:name, company_alias=:alias WHERE company_id=:id");
		writer.setDataSource(dataSource);
		return writer;
	}

	@Bean
	public Job importUserJob() {
		return jobBuilderFactory
				.get("importUserJob")
				.incrementer(new RunIdIncrementer())
				.start(step1()).on("FAILED").end()
				.from(step1()).on("COMPLETED").to(step2()).on("FAILED").end()
				.from(step2()).on("COMPLETED").to(step3()).end()
				.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Company, Company>chunk(10).reader(multiResourceItemReader())
				.processor(companyExistingItemProcessor()).writer(updater()).build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2").<Company, Company>chunk(10)
				.reader(multiResourceItemReader())
				.processor(companyNewItemProcessor())
				.writer(inserter()).build();
	}
	
	@Bean
	public Step step3() {
		return stepBuilderFactory.get("step3").tasklet(doneFileRenameTasklet()).build();
	}
	
	@Bean
	public DoneFileRenameTasklet doneFileRenameTasklet() {
		DoneFileRenameTasklet tasklet = new DoneFileRenameTasklet();
		tasklet.setResource(csvResource);
		return tasklet;
	}

}
