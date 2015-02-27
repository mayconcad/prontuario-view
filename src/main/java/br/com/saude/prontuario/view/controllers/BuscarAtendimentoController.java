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

import br.com.saude.prontuario.model.entities.Atendimento;
import br.com.saude.prontuario.model.entities.Paciente;
import br.com.saude.prontuario.model.entities.Sala;
import br.com.saude.prontuario.model.entities.TipoAtendimento;
import br.com.saude.prontuario.model.enums.ResultMessages;
import br.com.saude.prontuario.model.springsecurity.entities.Role;
import br.com.saude.prontuario.service.interfaces.AclService;
import br.com.saude.prontuario.service.interfaces.AtendimentoService;
import br.com.saude.prontuario.service.interfaces.PacienteService;
import br.com.saude.prontuario.service.interfaces.RepasseService;
import br.com.saude.prontuario.service.interfaces.ResponsavelService;
import br.com.saude.prontuario.service.interfaces.SalaService;
import br.com.saude.prontuario.service.interfaces.TipoAtendimentoService;
import br.com.saude.prontuario.view.utils.UtilsView;

@Controller
@ViewScoped
public class BuscarAtendimentoController extends BaseController {

	private static final long serialVersionUID = 6005013660763485425L;

	@Inject
	private AtendimentoService atendimentoService;

	@Autowired
	private PacienteService pacienteService;
	
	@Autowired
	private SalaService salaService;

	@Autowired
	private TipoAtendimentoService tipoAtendimentoService;

	@ManagedProperty("#{aclServiceImpl}")
	private AclService aclService;

	private Atendimento atendimentoBusca;
	private Atendimento atendimentoRemove;
	private Atendimento atendimentoEdite;

	private Date dataInicial;
	private Date dataFinal;

	private String atendimentonameEdite;
	private String passwordEdite;

	private Role roleEdite;

	private List<Atendimento> atendimentos = new ArrayList<Atendimento>();
	
	private Paciente paciente;

	private TipoAtendimento tipoAtendimento;
	
	private Sala sala;

	@PostConstruct
	public void init() {
		atendimentos = new ArrayList<Atendimento>();
		roleEdite = new Role();
		this.atendimentoBusca = this.atendimentoEdite = this.atendimentoRemove = new Atendimento();
		atendimentonameEdite = passwordEdite = new String();
	}

	public void editar(ActionEvent actionEvent) {

		try {
			atendimentoService.editar(this.atendimentoEdite);
		} catch (Exception e) {
			addErrorMessage(ResultMessages.ERROR_CRUD.getDescricao()
					+ e.getLocalizedMessage());
			return;
		}

		addInfoMessage(ResultMessages.UPDATE_SUCESS.getDescricao());
		loadToFind();
		this.atendimentoEdite = new Atendimento();
	}

	public void buscar() {

		atendimentos.clear();
		Map<String, Object> params = new HashMap<String, Object>();
		if (atendimentoBusca.getDescricao() != null
				&& !"".equals(atendimentoBusca.getDescricao()))
			params.put("nome", atendimentoBusca.getDescricao());
		// if (atendimentoBusca.getAtendimentoname() != null
		// && !"".equals(atendimentoBusca.getAtendimentoname()))
		// params.put("atendimentoname", atendimentoBusca.getAtendimentoname());
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
		atendimentos = atendimentoService.buscar(params);
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
					"atendimentoTabView:buscarAtendimentoForm");
			return;
		}

		atendimentoService.remover(atendimentoRemove);
		RequestContext.getCurrentInstance().update(
				"atendimentoTabView:buscarAtendimentoForm");
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
	
	public AtendimentoService getAtendimentoService() {
		return atendimentoService;
	}

	public void setAtendimentoService(AtendimentoService atendimentoService) {
		this.atendimentoService = atendimentoService;
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

	public Atendimento getAtendimentoBusca() {
		return atendimentoBusca;
	}

	public void setAtendimentoBusca(Atendimento atendimentoBusca) {
		this.atendimentoBusca = atendimentoBusca;
	}

	public Atendimento getAtendimentoRemove() {
		return atendimentoRemove;
	}

	public void setAtendimentoRemove(Atendimento atendimentoRemove) {
		this.atendimentoRemove = atendimentoRemove;
	}

	public Atendimento getAtendimentoEdite() {
		return atendimentoEdite;
	}

	public void setAtendimentoEdite(Atendimento atendimentoEdite) {
		this.atendimentoEdite = atendimentoEdite;
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

	public List<Atendimento> getAtendimentos() {
		return atendimentos;
	}

	public void setAtendimentos(List<Atendimento> atendimentos) {
		this.atendimentos = atendimentos;
	}

	public String getAtendimentonameEdite() {
		return atendimentonameEdite;
	}

	public void setAtendimentonameEdite(String atendimentoname) {
		this.atendimentonameEdite = atendimentoname;
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
