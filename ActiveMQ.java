package com.hcl.product.version;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.jms.ConnectionFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import com.hcl.product.version.entity.Product;
import com.hcl.product.version.model.ProductModel;
import com.hcl.product.version.service.ReceiveService;

public class ActiveMQ {
	
	//In main application class:
		
		@Bean
		public JmsListenerContainerFactory<?> myFactory(
				ConnectionFactory connectionFactory,
				DefaultJmsListenerContainerFactoryConfigurer configurer) {
			DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
			configurer.configure(factory, connectionFactory);
			return factory;
		}

		@Bean
		public MessageConverter jacksonJmsMessageConverter() {
			MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
			converter.setTargetType(MessageType.TEXT);
			converter.setTypeIdPropertyName("_type");
			return converter;
		}
		
		//In source of msg generator:
		
		private static final String JMS_QUEUE = "jms.queue";
		private final JmsTemplate jmsTemplate;

		@Autowired
		public ProductServiceImpl(JmsTemplate jmsTemplate) {
			this.jmsTemplate = jmsTemplate;
		}

		@Override
		public void addProduct(ProductModel productModel) {
			System.out.println("Inside ProductServiceImpl..........................................................");
			jmsTemplate.convertAndSend(JMS_QUEUE, productModel);
			System.out.println("Message sent from ProductServiceImpl.........................................................." +productModel+ " ");
		}

		@Override
		public void run(String... args) throws Exception {

		}

		//In msg Listener:
		@Autowired
		private ReceiveService receiveService;

		@Autowired
		public ProductMessageListener(ReceiveService receiveService) {
			this.receiveService = receiveService;
		}

		@JmsListener(destination = "jms.queue", containerFactory= "myFactory")
		public void receiveProduct(ProductModel productModel) {
			System.out.println("Inside ProductMessageListener................................................................");
			receiveService.registerProduct(productModel);
		}
		
		//In receiver:
		
		private final List<ProductModel> receivedProductModelList = new ArrayList<>();

		public void registerProduct(ProductModel productModel) {

			Product product = new Product(); 

			System.out.println("Inside registerProduct method................................................................");

			this.receivedProductModelList.add(productModel);

			for(ProductModel productModel1 : receivedProductModelList) { 
				Optional<Product> productOptional = productRepository.findById(productModel1.getProductId());

				if(productOptional.isPresent()) { 
					product = productOptional.get(); 
				}

				if (!(productModel.getProductNumber().equals(product.getProductNumber()) &&
						productModel.getProductId().equals(product.getProductId()) &&
						productModel.getProductName().equals(product.getProductName()) &&
						productModel.getPrice().equals(product.getPrice()) &&
						productModel.getProductDescription().equals(product.getProductDescription()) )) {

					Product productNew = new Product();

					BeanUtils.copyProperties(productModel, productNew);

					product.setStatus("INACTIVE");
					productRepository.save(product);

					productNew.setVersion("V" + String.valueOf(Integer.parseInt(productNew.getProductId().substring(6)) + 1));
					productNew.setStatus("ACTIVE");
					productNew.setProductId(productNew.getProductId().substring(0, 6) + String.valueOf(Integer.parseInt(productNew.getProductId().substring(6, 7)) + 1));
					productRepository.save(productNew); 
				} 
			}
		}
}
