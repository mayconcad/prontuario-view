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
import br.com.saude.prontuario.model.entities.TipoAtendimento;
import br.com.saude.prontuario.model.enums.ResultMessages;
import br.com.saude.prontuario.model.springsecurity.entities.Role;
import br.com.saude.prontuario.service.interfaces.AclService;
import br.com.saude.prontuario.service.interfaces.PacienteService;
import br.com.saude.prontuario.service.interfaces.SalaService;
import br.com.saude.prontuario.service.interfaces.TipoAtendimentoService;
import br.com.saude.prontuario.view.utils.UtilsView;

@Controller
@ViewScoped
public class BuscarTipoAtendimentoController extends BaseController {

	private static final long serialVersionUID = 6005013660763485425L;
	
	@Autowired
	private PacienteService pacienteService;
	
	@Autowired
	private SalaService salaService;

	@Autowired
	private TipoAtendimentoService tipoAtendimentoService;

	@ManagedProperty("#{aclServiceImpl}")
	private AclService aclService;

	private TipoAtendimento tipoAtendimentoBusca;
	private TipoAtendimento tipoAtendimentoRemove;
	private TipoAtendimento tipoAtendimentoEdite;

	private Date dataInicial;
	private Date dataFinal;

	private String tipoAtendimentonameEdite;
	private String passwordEdite;

	private Role roleEdite;

	private List<TipoAtendimento> tipoAtendimentos = new ArrayList<TipoAtendimento>();
	
	private Paciente paciente;

	private TipoAtendimento tipoAtendimento;
	
	private Sala sala;

	@PostConstruct
	public void init() {
		tipoAtendimentos = new ArrayList<TipoAtendimento>();
		roleEdite = new Role();
		this.tipoAtendimentoBusca = this.tipoAtendimentoEdite = this.tipoAtendimentoRemove = new TipoAtendimento();
		tipoAtendimentonameEdite = passwordEdite = new String();
	}

	public void editar(ActionEvent actionEvent) {

		try {
			tipoAtendimentoService.editar(this.tipoAtendimentoEdite);
		} catch (Exception e) {
			addErrorMessage(ResultMessages.ERROR_CRUD.getDescricao()
					+ e.getLocalizedMessage());
			return;
		}

		addInfoMessage(ResultMessages.UPDATE_SUCESS.getDescricao());
		loadToFind();
		this.tipoAtendimentoEdite = new TipoAtendimento();
	}

	public void buscar() {

		tipoAtendimentos.clear();
		Map<String, Object> params = new HashMap<String, Object>();
		if (tipoAtendimentoBusca.getDescricao() != null
				&& !"".equals(tipoAtendimentoBusca.getDescricao()))
			params.put("nome", tipoAtendimentoBusca.getDescricao());
		// if (tipoAtendimentoBusca.getTipoAtendimentoname() != null
		// && !"".equals(tipoAtendimentoBusca.getTipoAtendimentoname()))
		// params.put("tipoAtendimentoname", tipoAtendimentoBusca.getTipoAtendimentoname());
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
		tipoAtendimentos = tipoAtendimentoService.buscar(params);
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
		tipoAtendimentoService.remover(tipoAtendimentoRemove);
		buscar();
		addInfoMessage(ResultMessages.DELETE_SUCESS.getDescricao());
	}	

	public List<Paciente> autocompletarPaciente(String valor) {
		return pacienteService.autocompletar(valor);
	}
	
	public List<TipoAtendimento> autocompletarTipoAtendimento(String valor) {
		return tipoAtendimentoService.autocompletar(valor);
	}
	
	public List<Sala> autocompletarSala(String valor) {
		return salaService.autocompletar(valor);
	}	
	

	public TipoAtendimentoService getTipoAtendimentoService() {
		return tipoAtendimentoService;
	}

	public void setTipoAtendimentoService(TipoAtendimentoService tipoAtendimentoService) {
		this.tipoAtendimentoService = tipoAtendimentoService;
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

	public TipoAtendimento getTipoAtendimento() {
		return tipoAtendimento;
	}

	public void setTipoAtendimento(TipoAtendimento tipoAtendimento) {
		this.tipoAtendimento = tipoAtendimento;
	}
	
	public SalaService getSalaService() {
		return salaService;
	}

	public void setSalaService(SalaService salaService) {
		this.salaService = salaService;
	}

	public Sala getSala() {
		return sala;
	}

	public void setSala(Sala sala) {
		this.sala = sala;
	}

	public TipoAtendimento getTipoAtendimentoBusca() {
		return tipoAtendimentoBusca;
	}

	public void setTipoAtendimentoBusca(TipoAtendimento tipoAtendimentoBusca) {
		this.tipoAtendimentoBusca = tipoAtendimentoBusca;
	}

	public TipoAtendimento getTipoAtendimentoRemove() {
		return tipoAtendimentoRemove;
	}

	public void setTipoAtendimentoRemove(TipoAtendimento tipoAtendimentoRemove) {
		this.tipoAtendimentoRemove = tipoAtendimentoRemove;
	}

	public TipoAtendimento getTipoAtendimentoEdite() {
		return tipoAtendimentoEdite;
	}

	public void setTipoAtendimentoEdite(TipoAtendimento tipoAtendimentoEdite) {
		this.tipoAtendimentoEdite = tipoAtendimentoEdite;
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

	public List<TipoAtendimento> getTipoAtendimentos() {
		return tipoAtendimentos;
	}

	public void setTipoAtendimentos(List<TipoAtendimento> tipoAtendimentos) {
		this.tipoAtendimentos = tipoAtendimentos;
	}

	public String getTipoAtendimentonameEdite() {
		return tipoAtendimentonameEdite;
	}

	public void setTipoAtendimentonameEdite(String tipoAtendimentoname) {
		this.tipoAtendimentonameEdite = tipoAtendimentoname;
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
