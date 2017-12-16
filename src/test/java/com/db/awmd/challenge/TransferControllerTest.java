package com.db.awmd.challenge;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.service.TransferService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TransferControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private TransferService transferService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void prepareMockMvc() {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
		// Reset the existing accounts before each test.
		transferService.getAccountsRepository().clearAccounts();
	}

	@Test
	public void transferAmount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
		
		this.mockMvc.perform(post("/v2/transfer/Id-123/Id-456/300"))
				.andExpect(status().isAccepted());
	}
	
	@Test
	public void transferExactAmount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
		
		this.mockMvc.perform(post("/v2/transfer/Id-123/Id-456/1000"))
				.andExpect(status().isAccepted());
	}
	
	@Test
	public void transferAmountToInvalidAccount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
		
		this.mockMvc.perform(post("/v2/transfer/Id-123/Id-450/300"))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void transferInvalidAmount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
		
		this.mockMvc.perform(post("/v2/transfer/Id-123/Id-450/abc"))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void transferNegativeAmount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
		
		this.mockMvc.perform(post("/v2/transfer/Id-123/Id-450/-300"))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void transferAmountGreaterThanBalanceAmount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());
		
		this.mockMvc.perform(post("/v2/transfer/Id-123/Id-450/3000"))
				.andExpect(status().isBadRequest());
	}
	

}
