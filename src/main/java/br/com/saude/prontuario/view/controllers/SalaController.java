package br.com.saude.prontuario.view.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.saude.prontuario.model.entities.Sala;
import br.com.saude.prontuario.model.entities.Responsavel;
import br.com.saude.prontuario.model.enums.ResultMessages;
import br.com.saude.prontuario.model.springsecurity.entities.Role;
import br.com.saude.prontuario.service.interfaces.SalaService;
import br.com.saude.prontuario.service.interfaces.ResponsavelService;
import br.com.saude.prontuario.service.interfaces.RoleService;
import br.com.saude.prontuario.view.utils.UtilsView;

@Controller
@ViewScoped
public class SalaController extends BaseController {

	private static final long serialVersionUID = 6005013660763485425L;

	@Autowired
	private SalaService salaService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private ResponsavelService responsavelService;

	private Sala sala;
	
	private List<Sala> salas = new ArrayList<Sala>();

	private String login;

	private String senha;

	private boolean enableAutocomplete;

	private Role role;

	private Responsavel responsavel;

	public SalaController() {
		responsavel = new Responsavel();
		role = new Role();
		sala = new Sala();
		login = senha = new String();
	}

	@PostConstruct
	public void init() {
		responsavel = new Responsavel();
		role = new Role();
		sala = new Sala();
		login = senha = new String();
	}

	public void save() {		
		
		try {
			salaService.criar(this.sala);			
		} catch (Exception e) {
			addErrorMessage(ResultMessages.ERROR_CRUD.getDescricao()
					+ e.getLocalizedMessage());
			return;
		}
		this.sala = new Sala();
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

	public Sala getSala() {
		return sala;
	}

	public void setSala(Sala sala) {
		this.sala = sala;
	}

	public SalaService getSalaService() {
		return salaService;
	}

	public void setSalaService(SalaService salaService) {
		this.salaService = salaService;
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
	
	public List<Sala> getSalas() {
		return salas;
	}

	public void setSalas(List<Sala> salas) {
		this.salas = salas;
	}

	public Sala getSalaById(long id) {
		for (Sala uc : getSalas()) {
			if (uc.getId() == id)
				return uc;
		}
		return null;
	}
}
