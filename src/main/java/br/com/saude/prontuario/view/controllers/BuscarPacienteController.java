package br.com.saude.prontuario.view.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.saude.prontuario.model.entities.Paciente;
import br.com.saude.prontuario.model.enums.ResultMessages;
import br.com.saude.prontuario.model.springsecurity.entities.Role;
import br.com.saude.prontuario.service.interfaces.AclService;
import br.com.saude.prontuario.service.interfaces.PacienteService;
import br.com.saude.prontuario.service.interfaces.RepasseService;
import br.com.saude.prontuario.service.interfaces.ResponsavelService;
import br.com.saude.prontuario.view.utils.UtilsView;

@Controller
@ViewScoped
public class BuscarPacienteController extends BaseController {

	private static final long serialVersionUID = 6005013660763485425L;

	@Inject
	private PacienteService pacienteService;

	@Autowired
	private RepasseService repasseService;

	@Autowired
	private ResponsavelService responsavelService;

	@ManagedProperty("#{aclServiceImpl}")
	private AclService aclService;

	private Paciente pacienteBusca;
	private Paciente pacienteRemove;
	private Paciente pacienteEdite;

	private Date dataInicial;
	private Date dataFinal;

	private String pacientenameEdite;
	private String passwordEdite;

	private Role roleEdite;

	private List<Paciente> pacientes = new ArrayList<Paciente>();

	@PostConstruct
	public void init() {
		pacientes = new ArrayList<Paciente>();
		roleEdite = new Role();
		this.pacienteBusca = this.pacienteEdite = this.pacienteRemove = new Paciente();
		pacientenameEdite = passwordEdite = new String();
	}

	public void editar(ActionEvent actionEvent) {

		try {
			pacienteService.editar(this.pacienteEdite);
		} catch (Exception e) {
			addErrorMessage(ResultMessages.ERROR_CRUD.getDescricao()
					+ e.getLocalizedMessage());
			return;
		}

		addInfoMessage(ResultMessages.UPDATE_SUCESS.getDescricao());
		loadToFind();
		this.pacienteEdite = new Paciente();
	}

	public void buscar() {

		pacientes.clear();
		Map<String, Object> params = new HashMap<String, Object>();
		if (pacienteBusca.getNome() != null
				&& !"".equals(pacienteBusca.getNome()))
			params.put("nome", pacienteBusca.getNome());
		// if (pacienteBusca.getPacientename() != null
		// && !"".equals(pacienteBusca.getPacientename()))
		// params.put("pacientename", pacienteBusca.getPacientename());
		if (getDataInicial() != null)
			params.put("dataInicial", getDataInicial());
		if (getDataFinal() != null)
			params.put("dataFinal", getDataFinal());
		String principalRole = UtilsView.getControllerInstance(
				LoginController.class).getPrincipalRole();
		if (principalRole != null && !principalRole.equals("ADMIN"))
			params.put("principalRole",
					UtilsView.getControllerInstance(LoginController.class)
							.getPrincipalRole());
		pacientes = pacienteService.buscar(params);
	}

	public void loadToFind() {
		getEditTab().setRendered(false);
		TabView parent = (TabView) getFindTab().getParent();
		int findIndex = parent.getChildren().indexOf(getFindTab());
		parent.setActiveIndex(findIndex);
	}
	
	public void carregar() {
		getEditTab().setRendered(true);
		TabView parent = (TabView) getEditTab().getParent();
		int editIndex = parent.getChildren().indexOf(getEditTab());
		parent.setActiveIndex(editIndex);		
	}


	public void remover() {

		if (usuarioSemPermissao()) {

			RequestContext.getCurrentInstance().update(
					"pacienteTabView:buscarPacienteForm");
			return;
		}

		pacienteService.remover(pacienteRemove);
		buscar();
		addInfoMessage(ResultMessages.DELETE_SUCESS.getDescricao());
	}

	public PacienteService getPacienteService() {
		return pacienteService;
	}

	public void setPacienteService(PacienteService pacienteService) {
		this.pacienteService = pacienteService;
	}

	public RepasseService getRepasseService() {
		return repasseService;
	}

	public void setRepasseService(RepasseService repasseService) {
		this.repasseService = repasseService;
	}

	public ResponsavelService getResponsavelService() {
		return responsavelService;
	}

	public void setResponsavelService(ResponsavelService responsavelService) {
		this.responsavelService = responsavelService;
	}

	public Paciente getPacienteBusca() {
		return pacienteBusca;
	}

	public void setPacienteBusca(Paciente pacienteBusca) {
		this.pacienteBusca = pacienteBusca;
	}

	public Paciente getPacienteRemove() {
		return pacienteRemove;
	}

	public void setPacienteRemove(Paciente pacienteRemove) {
		this.pacienteRemove = pacienteRemove;
	}

	public Paciente getPacienteEdite() {
		return pacienteEdite;
	}

	public void setPacienteEdite(Paciente pacienteEdite) {
		this.pacienteEdite = pacienteEdite;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public List<Paciente> getPacientes() {
		return pacientes;
	}

	public void setPacientes(List<Paciente> pacientes) {
		this.pacientes = pacientes;
	}

	public String getPacientenameEdite() {
		return pacientenameEdite;
	}

	public void setPacientenameEdite(String pacientename) {
		this.pacientenameEdite = pacientename;
	}

	public String getPasswordEdite() {
		return passwordEdite;
	}

	public void setPasswordEdite(String password) {
		this.passwordEdite = password;
	}

	public Role getRoleEdite() {
		return roleEdite;
	}

	public void setRoleEdite(Role roleEdite) {
		this.roleEdite = roleEdite;
	}

}
