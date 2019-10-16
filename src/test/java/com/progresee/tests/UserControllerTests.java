package com.progresee.tests;
//package com.end.project.End_Project;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.CoreMatchers.containsString;
//import static org.hamcrest.Matchers.hasSize;
//import static org.hamcrest.Matchers.is;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import java.util.ArrayList;
//import java.util.List;
//import static org.mockito.Mockito.*;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import com.end.project.End_Project.Beans.User;
//import com.end.project.End_Project.Controllers.UserController;
//import com.end.project.End_Project.Repositories.UserRepository;
//import com.end.project.End_Project.Services.UserService;
//import com.end.project.End_Project.Utils.TestUtils;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//@EnableWebMvc
//public class MemeberControllerTests {
//
//	@LocalServerPort
//	private int port;
//
//	@Autowired
//	private MockMvc mockMvc;
//
//	@Autowired
//	private UserController controller;
//	@Autowired
//	private UserRepository userRepo;
//	@Autowired
//	private UserService userService;
//
//	private List<User> users=new ArrayList<>();
//
//	@Before
//	public void beforeTests() {
////		membmerRepo.deleteAll();
//		User user=new User("user@","1234"
//				,"ROLE_USER,ROLE_ADMIN"
//				,true
//				,null
//				,"chen"
//				,"ben ami"
//				,null);
//		
////		membmerRepo.save(member);
//		User user2=new User("user1@","1234"
//				,"ROLE_USER,ROLE_ADMIN"
//				,true
//				,null
//				,"chen"
//				,"ben ami"
//				,null);
////		membmerRepo.save(member2);
//		users.add(user);
//		users.add(user2);
////
//	}
//
//	@Test
//	public void nullCheck() {
//		assertThat(this.controller).isNotNull();
//	}
//
//	@Test
//	public void getMemberTest() throws Exception {
//		long id =6;
//		mockMvc.perform(get("/user/getUser/"+id)
//			    .contentType("application/json"))
//			    .andExpect(status().isOk())
//			    .andExpect(jsonPath("$.id", is(id)))
//		        .andExpect(jsonPath("$.firstName", is("chen2")))
//		        .andExpect(jsonPath("$.lastName", is("ben ami")));
//	}
//
//	@Test
//	public void getMembersTest() throws Exception {
//		mockMvc.perform(get("/user/getAll")
//			    .contentType("application/json"))
//			    .andExpect(status().isOk())
//			    .andExpect(jsonPath("$", hasSize(2)));
//
//	}
//
//	@Test
//	public void createMemberTest() throws Exception {
//		User user=new User("user2@","1234"
//				,"ROLE_USER,ROLE_ADMIN"
//				,true
//				,null
//				,"sean"
//				,"hed"
//				,null);
//		mockMvc.perform(post("/user/createUser")
//				.accept(MediaType.APPLICATION_JSON_UTF8)
//	            .contentType(MediaType.APPLICATION_JSON_UTF8)
//				.content(TestUtils.asJsonString(user)))
//		        .andExpect(status().isOk())
//		        .andExpect(jsonPath("$.firstName", is("sean")))
//		        .andExpect(jsonPath("$.lastName", is("hed")));
//
//	}
//
//
//	@Test
//	public void deleteMemberTest() throws Exception {
//	long id=9;
//		mockMvc.perform(delete("/user/deleteMember/"+id))
//		        .andExpect(status().isOk())
//		        .andExpect(content().string(containsString("User with id "+id)));
//
//	}
//
//
//	 @Test
//	 public void updateMemberTest() throws Exception {
//		 User user=new User(1,"user@","1234"
//					,"ROLE_USER,ROLE_ADMIN"
//					,true
//					,null
//					,"chen2"
//					,"benami"
//					,null);
//		 mockMvc.perform(put("/user/updateUser")
//		 .accept(MediaType.APPLICATION_JSON_UTF8)
//         .contentType(MediaType.APPLICATION_JSON_UTF8)
//		 .content(TestUtils.asJsonString(user)))
//		 .andExpect(status().isOk())
//	     .andExpect(jsonPath("$.firstName", is("chen2")))
//		 .andExpect(jsonPath("$.lastName", is("benami")));
//	 }
//
//
//
//
//}
