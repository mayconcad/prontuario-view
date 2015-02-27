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

import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.saude.prontuario.model.entities.Paciente;
import br.com.saude.prontuario.model.entities.Sala;
import br.com.saude.prontuario.model.enums.ResultMessages;
import br.com.saude.prontuario.model.springsecurity.entities.Role;
import br.com.saude.prontuario.service.interfaces.AclService;
import br.com.saude.prontuario.service.interfaces.PacienteService;
import br.com.saude.prontuario.service.interfaces.SalaService;
import br.com.saude.prontuario.view.utils.UtilsView;

@Controller
@ViewScoped
public class BuscarSalaController extends BaseController {

	private static final long serialVersionUID = 6005013660763485425L;
	
	@Autowired
	private PacienteService pacienteService;
	
	@Autowired
	private SalaService salaService;	

	@ManagedProperty("#{aclServiceImpl}")
	private AclService aclService;

	private Sala salaBusca;
	private Sala salaRemove;
	private Sala salaEdite;

	private Date dataInicial;
	private Date dataFinal;

	private String salanameEdite;
	private String passwordEdite;

	private Role roleEdite;

	private List<Sala> salas = new ArrayList<Sala>();
	
	private Paciente paciente;

	private Sala sala;

	@PostConstruct
	public void init() {
		salas = new ArrayList<Sala>();
		roleEdite = new Role();
		this.salaBusca = this.salaEdite = this.salaRemove = new Sala();
		salanameEdite = passwordEdite = new String();
	}

	public void editar(ActionEvent actionEvent) {

		try {
			salaService.editar(this.salaEdite);
		} catch (Exception e) {
			addErrorMessage(ResultMessages.ERROR_CRUD.getDescricao()
					+ e.getLocalizedMessage());
			return;
		}

		addInfoMessage(ResultMessages.UPDATE_SUCESS.getDescricao());
		loadToFind();
		this.salaEdite = new Sala();
	}

	public void buscar() {

		salas.clear();
		Map<String, Object> params = new HashMap<String, Object>();
		if (salaBusca.getDescricao() != null
				&& !"".equals(salaBusca.getDescricao()))
			params.put("nome", salaBusca.getDescricao());
		// if (salaBusca.getSalaname() != null
		// && !"".equals(salaBusca.getSalaname()))
		// params.put("salaname", salaBusca.getSalaname());
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
		salas = salaService.buscar(params);
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

		salaService.remover(salaRemove);
		buscar();
		addInfoMessage(ResultMessages.DELETE_SUCESS.getDescricao());
	}	

	public List<Paciente> autocompletarPaciente(String valor) {
		return pacienteService.autocompletar(valor);
	}
	
	public List<Sala> autocompletarSala(String valor) {
		return salaService.autocompletar(valor);
	}
		

	public SalaService getSalaService() {
		return salaService;
	}

	public void setSalaService(SalaService tipoSalaService) {
		this.salaService = salaService;
	}	

	public PacienteService getPacienteService() {
		return pacienteService;
	}

	public void setPacienteService(PacienteService pacienteService) {
		this.pacienteService = pacienteService;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}	

	public Sala getSalaBusca() {
		return salaBusca;
	}

	public void setSalaBusca(Sala salaBusca) {
		this.salaBusca = salaBusca;
	}

	public Sala getSalaRemove() {
		return salaRemove;
	}

	public void setSalaRemove(Sala salaRemove) {
		this.salaRemove = salaRemove;
	}

	public Sala getSalaEdite() {
		return salaEdite;
	}

	public void setSalaEdite(Sala salaEdite) {
		this.salaEdite = salaEdite;
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

	public List<Sala> getSalas() {
		return salas;
	}

	public void setSalas(List<Sala> salas) {
		this.salas = salas;
	}

	public String getSalanameEdite() {
		return salanameEdite;
	}

	public void setSalanameEdite(String salaname) {
		this.salanameEdite = salaname;
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
