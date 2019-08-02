package com.hcl.product.version.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.hcl.product.version.entity.Product;
import com.hcl.product.version.model.ProductModel;

@Component
public class LoadObjectUtils
{	
	public List<Product> mappingExcelToProduct(String filePath)
	{
		final String FILE_NAME = filePath;//"D:/product.xlsx";
		List<Product> productList = new ArrayList<>();
		
		List<String> headerList = new ArrayList<>();
		headerList.add("productId");
		headerList.add("productNumber");
		headerList.add("productName");
		headerList.add("productDescription");
		headerList.add("price");
		headerList.add("releaseDate");
		headerList.add("version");
		headerList.add("status");
		
		FileInputStream excelFile = null;
		try {
			excelFile = new FileInputStream(new File(FILE_NAME));
		
		
		try(Workbook workbook = new XSSFWorkbook(excelFile)) 
		{
			

			
            
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            Map<String, Integer> headaerMap = new HashMap<>();
            
            while (iterator.hasNext()) {

                Row currentRow = iterator.next();
                
                Iterator<Cell> cellIterator = currentRow.iterator();
                
                Cell header = null;
                
                if(currentRow.getRowNum() == 0)
                {
                	while(cellIterator.hasNext())
                    {
                		header = cellIterator.next();
                    
                
                	if(header.getStringCellValue().equalsIgnoreCase(headerList.get(0)))
                		headaerMap.put(headerList.get(0), header.getColumnIndex());
                	
                	else if(header.getStringCellValue().equalsIgnoreCase(headerList.get(1)))
                		headaerMap.put(headerList.get(1), header.getColumnIndex());
                	
                	else if(header.getStringCellValue().equalsIgnoreCase(headerList.get(2)))
                		headaerMap.put(headerList.get(2), header.getColumnIndex());
                	
                	else if(header.getStringCellValue().equalsIgnoreCase(headerList.get(3)))
                		headaerMap.put(headerList.get(3), header.getColumnIndex());
                	
                	else if(header.getStringCellValue().equalsIgnoreCase(headerList.get(4)))
                		headaerMap.put(headerList.get(4), header.getColumnIndex());
                	
                	else if(header.getStringCellValue().equalsIgnoreCase(headerList.get(5)))
                		headaerMap.put(headerList.get(5), header.getColumnIndex());
                	
                	else if(header.getStringCellValue().equalsIgnoreCase(headerList.get(6)))
                		headaerMap.put(headerList.get(6), header.getColumnIndex());
                	
                	else if(header.getStringCellValue().equalsIgnoreCase(headerList.get(7)))
                		headaerMap.put(headerList.get(7), header.getColumnIndex());
                	
                	
                    }
                }
                else
                {
                	ProductModel model = new ProductModel();
                	
                	Cell productNameCells = currentRow.getCell(headaerMap.get(headerList.get(2)));
                	Cell productIdCells = currentRow.getCell(headaerMap.get(headerList.get(0)));
                	Cell productNumberCells = currentRow.getCell(headaerMap.get(headerList.get(1)));
                	Cell productDescriptionCells = currentRow.getCell(headaerMap.get(headerList.get(3)));
                	Cell priceCells = currentRow.getCell(headaerMap.get(headerList.get(4)));
                	Cell releaseDateCells = currentRow.getCell(headaerMap.get(headerList.get(5)));
                	Cell versionCells = currentRow.getCell(headaerMap.get(headerList.get(6)));
                	Cell statusCells = currentRow.getCell(headaerMap.get(headerList.get(7)));
                	
                	if(productIdCells != null) 
                            model.setProductId(productIdCells.getStringCellValue());
                	if(productNameCells != null) 
                            model.setProductName(productNameCells.getStringCellValue());
					
					  if(priceCells != null)
					  model.setPrice(priceCells.getNumericCellValue());
					 
                	if(productNumberCells != null) 
                        model.setProductNumber(productNumberCells.getStringCellValue());
                	if(productDescriptionCells != null) 
                        model.setProductDescription(productDescriptionCells.getStringCellValue());
					
					/*
					 * if(releaseDateCells != null)
					 * model.setReleaseDate(releaseDateCells.getDateCellValue());
					 */
					 
                	if(versionCells != null) 
                        model.setVersion(versionCells.getStringCellValue());
                	if(statusCells != null) 
                        model.setStatus(statusCells.getStringCellValue());
                	
                	
                	Product product = new Product();
                	BeanUtils.copyProperties(model, product);
                	productList.add(product);
                }
            }

		} 
		catch (IOException e) {
		} 
		
		} catch (FileNotFoundException e1) {
		}finally {
			try {
				excelFile.close();
			} catch (IOException e) {
			}
		}

		return productList;
	}

}