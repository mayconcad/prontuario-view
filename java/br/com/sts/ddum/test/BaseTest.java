package br.com.saude.prontuario.test;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import br.com.saude.prontuario.model.entities.UserSystem;
import br.com.saude.prontuario.model.repository.interfaces.UserRepository;

@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@Transactional
// @RunWith(SpringJUnit4ClassRunner.class)
public class BaseTest {

	@Inject
	private UserRepository userRepository;

	// @Test
	public void test() {

		UserSystem user = new UserSystem();

		user.getId();

		userRepository.save(user);

	}

}
