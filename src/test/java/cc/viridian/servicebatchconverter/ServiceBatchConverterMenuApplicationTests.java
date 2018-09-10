package cc.viridian.servicebatchconverter;

import cc.viridian.servicebatchconverter.run.BatchConverterMenu;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.IOException;
import java.net.URL;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceBatchConverterApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ServiceBatchConverterMenuApplicationTests {

	URL sampleFileUrl;


	BatchConverterMenu batchConverterMenu;

	@Before
	public void setUp() throws Exception {
		//"setup test"
        batchConverterMenu = new  BatchConverterMenu();
		sampleFileUrl = ServiceBatchConverterMenuApplicationTests.class
			.getClass().getResource("/files/Statement_1998-01-01_2017-12-31.prn");
	}

	@Test
	public void contextLoads() throws IOException {
		System.out.println("HOLA");
		batchConverterMenu.useTestFile(sampleFileUrl.getPath().toString());
	}

}
