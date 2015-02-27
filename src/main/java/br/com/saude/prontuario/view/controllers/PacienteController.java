package br.com.saude.prontuario.view.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.saude.prontuario.model.entities.Paciente;
import br.com.saude.prontuario.model.entities.Responsavel;
import br.com.saude.prontuario.model.enums.ResultMessages;
import br.com.saude.prontuario.model.springsecurity.entities.Role;
import br.com.saude.prontuario.model.springsecurity.entities.User;
import br.com.saude.prontuario.service.interfaces.PacienteService;
import br.com.saude.prontuario.service.interfaces.ResponsavelService;
import br.com.saude.prontuario.service.interfaces.RoleService;
import br.com.saude.prontuario.view.utils.UtilsView;

@Controller
@ViewScoped
public class PacienteController extends BaseController {

	private static final long serialVersionUID = 6005013660763485425L;

	@Autowired
	private PacienteService pacienteService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private ResponsavelService responsavelService;

	private Paciente paciente;
	
	private List<Paciente> pacientes = new ArrayList<Paciente>();

	private String login;

	private String senha;

	private boolean enableAutocomplete;

	private Role role;

	private Responsavel responsavel;

	public PacienteController() {
		responsavel = new Responsavel();
		role = new Role();
		paciente = new Paciente();
		login = senha = new String();
	}

	@PostConstruct
	public void init() {
		responsavel = new Responsavel();
		role = new Role();
		paciente = new Paciente();
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

		
		try {
			pacienteService.criar(this.paciente);			
		} catch (Exception e) {
			addErrorMessage(ResultMessages.ERROR_CRUD.getDescricao()
					+ e.getLocalizedMessage());
			return;
		}
		this.paciente = new Paciente();
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

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	public PacienteService getPacienteService() {
		return pacienteService;
	}

	public void setPacienteService(PacienteService pacienteService) {
		this.pacienteService = pacienteService;
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
	
	public List<Paciente> getPacientes() {
		return pacientes;
	}

	public void setPacientes(List<Paciente> pacientes) {
		this.pacientes = pacientes;
	}

	public Paciente getPacienteById(long id) {
		for (Paciente uc : getPacientes()) {
			if (uc.getId() == id)
				return uc;
		}
		return null;
	}
}
