package br.com.saude.prontuario.view.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.saude.prontuario.model.entities.Responsavel;
import br.com.saude.prontuario.model.enums.ResultMessages;
import br.com.saude.prontuario.model.springsecurity.entities.Role;
import br.com.saude.prontuario.model.springsecurity.entities.User;
import br.com.saude.prontuario.service.interfaces.ResponsavelService;
import br.com.saude.prontuario.service.interfaces.RoleService;
import br.com.saude.prontuario.service.interfaces.UserService;
import br.com.saude.prontuario.view.utils.UtilsView;

@Controller
@ViewScoped
public class UserController extends BaseController {

	private static final long serialVersionUID = 6005013660763485425L;

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private ResponsavelService responsavelService;

	private User user;

	private String login;

	private String senha;

	private boolean enableAutocomplete;

	private Role role;

	private Responsavel responsavel;

	public UserController() {
		responsavel = new Responsavel();
		role = new Role();
		user = new User();
		login = senha = new String();
	}

	@PostConstruct
	public void init() {
		responsavel = new Responsavel();
		role = new Role();
		user = new User();
		login = senha = new String();
	}

	public void save() {

		LoginBean controllerInstance = UtilsView
				.getControllerInstance(LoginBean.class);
		if (!controllerInstance.getPrincipalRole().equals("ADMIN")
				&& !controllerInstance.getPrincipalRole().equals("GESTOR")) {
			addErrorMessage(ResultMessages.ERROR_ONLY_ADMIN_AND_GESTOR_OPERATION
					.getDescricao());
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		if (getLogin() != null && !"".equals(getLogin()))
			params.put("username", getLogin());
		
		List<User> usuarios = userService.buscar(params);
		
		if(usuarios != null && usuarios.size() > 0){
			addErrorMessage("Já existe um usuário com este login.");
			return;
		}

		boolean possuiResponsavel = responsavel != null
				&& responsavel.getId() != null && responsavel.getId() > 0;

		List<Role> roles = new ArrayList<Role>();
		roles.add(role);
		this.user.setRoles(roles);
		if (possuiResponsavel)
			this.user.setName(responsavel.getNome());
		this.user.setUsername(getLogin());
		this.user.setPassword(getSenha());
		this.user.setCreatedAt(new Date());
		this.user.setAtivo(true);
		this.user.setId(0l);
		try {
			userService.save(this.user);
			if (possuiResponsavel) {
				responsavel.setUser(getUser());
				responsavelService.atualizar(responsavel);
			}
		} catch (Exception e) {
			addErrorMessage(ResultMessages.ERROR_CRUD.getDescricao()
					+ e.getLocalizedMessage());
			return;
		}
		this.user = new User();
		this.responsavel = new Responsavel();
		this.role = new Role();
		login = senha = new String();

		addInfoMessage(ResultMessages.CREATE_SUCESS.getDescricao());
	}

	public void enableField() {
		if (role.getName() != null
				&& role.getName().trim().equals("RESPONSÁVEL"))
			enableAutocomplete = true;
		else
			enableAutocomplete = false;
	}

	public List<Role> autocompletarRole(String valor) {
		return roleService.autocompletar(valor);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public ResponsavelService getResponsavelService() {
		return responsavelService;
	}

	public void setResponsavelService(ResponsavelService responsavelService) {
		this.responsavelService = responsavelService;
	}

	public RoleService getRoleService() {
		return roleService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Responsavel getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Responsavel responsavel) {
		this.responsavel = responsavel;
	}

	public boolean getEnableAutocomplete() {
		return enableAutocomplete;
	}

	public void setEnableAutocomplete(boolean enableAutocomplete) {
		this.enableAutocomplete = enableAutocomplete;
	}

}
