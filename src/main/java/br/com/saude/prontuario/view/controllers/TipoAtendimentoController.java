package br.com.saude.prontuario.view.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.saude.prontuario.model.entities.TipoAtendimento;
import br.com.saude.prontuario.model.entities.Responsavel;
import br.com.saude.prontuario.model.enums.ResultMessages;
import br.com.saude.prontuario.model.springsecurity.entities.Role;
import br.com.saude.prontuario.service.interfaces.TipoAtendimentoService;
import br.com.saude.prontuario.service.interfaces.ResponsavelService;
import br.com.saude.prontuario.service.interfaces.RoleService;
import br.com.saude.prontuario.view.utils.UtilsView;

@Controller
@ViewScoped
public class TipoAtendimentoController extends BaseController {

	private static final long serialVersionUID = 6005013660763485425L;

	@Autowired
	private TipoAtendimentoService tipoAtendimentoService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private ResponsavelService responsavelService;

	private TipoAtendimento tipoAtendimento;
	
	private List<TipoAtendimento> tipoAtendimentos = new ArrayList<TipoAtendimento>();

	private String login;

	private String senha;

	private boolean enableAutocomplete;

	private Role role;

	private Responsavel responsavel;

	public TipoAtendimentoController() {
		responsavel = new Responsavel();
		role = new Role();
		tipoAtendimento = new TipoAtendimento();
		login = senha = new String();
	}

	@PostConstruct
	public void init() {
		responsavel = new Responsavel();
		role = new Role();
		tipoAtendimento = new TipoAtendimento();
		login = senha = new String();
	}

	public void save() {

			
		try {
			tipoAtendimentoService.criar(this.tipoAtendimento);			
		} catch (Exception e) {
			addErrorMessage(ResultMessages.ERROR_CRUD.getDescricao()
					+ e.getLocalizedMessage());
			return;
		}
		this.tipoAtendimento = new TipoAtendimento();
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

	public TipoAtendimento getTipoAtendimento() {
		return tipoAtendimento;
	}

	public void setTipoAtendimento(TipoAtendimento tipoAtendimento) {
		this.tipoAtendimento = tipoAtendimento;
	}

	public TipoAtendimentoService getTipoAtendimentoService() {
		return tipoAtendimentoService;
	}

	public void setTipoAtendimentoService(TipoAtendimentoService tipoAtendimentoService) {
		this.tipoAtendimentoService = tipoAtendimentoService;
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
	
	public List<TipoAtendimento> getTipoAtendimentos() {
		return tipoAtendimentos;
	}

	public void setTipoAtendimentos(List<TipoAtendimento> tipoAtendimentos) {
		this.tipoAtendimentos = tipoAtendimentos;
	}

	public TipoAtendimento getTipoAtendimentoById(long id) {
		for (TipoAtendimento uc : getTipoAtendimentos()) {
			if (uc.getId() == id)
				return uc;
		}
		return null;
	}
}
