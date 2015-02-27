package br.com.saude.prontuario.view.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;

import org.exolab.castor.types.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.saude.prontuario.model.entities.Atendimento;
import br.com.saude.prontuario.model.entities.Paciente;
import br.com.saude.prontuario.model.entities.Sala;
import br.com.saude.prontuario.model.entities.Sessao;
import br.com.saude.prontuario.model.entities.TipoAtendimento;
import br.com.saude.prontuario.model.enums.ResultMessages;
import br.com.saude.prontuario.service.interfaces.AtendimentoService;
import br.com.saude.prontuario.service.interfaces.PacienteService;
import br.com.saude.prontuario.service.interfaces.SalaService;
import br.com.saude.prontuario.service.interfaces.SessaoService;
import br.com.saude.prontuario.service.interfaces.TipoAtendimentoService;
import br.com.saude.prontuario.view.utils.UtilsView;

@Controller
@ViewScoped
public class AtendimentoController extends BaseController {

	private static final long serialVersionUID = 6005013660763485425L;

	@Autowired
	private AtendimentoService atendimentoService;

	@Autowired
	private PacienteService pacienteService;

	@Autowired
	private SessaoService sessaoService;

	@Autowired
	private SalaService salaService;

	@Autowired
	private TipoAtendimentoService tipoAtendimentoService;

	private Atendimento atendimento;

	private Sessao sessao;

	private Sessao sessaoEditar;

	private Sessao sessaoRemover;

	private List<Atendimento> atendimentos = new ArrayList<Atendimento>();

	private List<Sessao> sessoes = new ArrayList<Sessao>();

	private String login;

	private String senha;

	private boolean enableAutocomplete;

	private Paciente paciente;

	private TipoAtendimento tipoAtendimento;

	private Sala sala;

	private int contadorPasses = 0;

	private int contadorPassesSala01 = 0;
	private int contadorPassesSala02 = 0;

	public AtendimentoController() {
		tipoAtendimento = new TipoAtendimento();
		paciente = new Paciente();
		atendimento = new Atendimento();
		sala = new Sala();
		sessao = new Sessao();
		login = senha = new String();
		sessoes.clear();
		contadorPasses = 0;
		contadorPassesSala02 = 0;
		contadorPassesSala01 = 0;
	}

	@PostConstruct
	public void init() {
		tipoAtendimento = new TipoAtendimento();
		paciente = new Paciente();
		atendimento = new Atendimento();
		sala = new Sala();
		sessao = new Sessao();
		login = senha = new String();
		sessoes.clear();
		contadorPasses = 0;
		contadorPassesSala02 = 0;
		contadorPassesSala01 = 0;
	}

	public void save() {

		LoginBean controllerInstance = UtilsView
				.getControllerInstance(LoginBean.class);
		List<Sessao> sessoesUpdate = new ArrayList<Sessao>();
		try {
			atendimento.setPaciente(paciente);
			atendimento.setTipoAtendimento(tipoAtendimento);

			LoginBean loginController = UtilsView
					.getControllerInstance(LoginBean.class);
			
			if (atendimento.getId() == null) {
				atendimento.setLog(String.format("CRIA atendimento(paciente: %s; tipoatend: %s; data: %s; usuário: %s",paciente.getNome(),tipoAtendimento.getDescricao(),  new DateTime().toString() ,loginController.getUserName()));
				atendimentoService.criar(this.atendimento);
			}else
				atendimento.setLog(String.format("EDITA atendimento(paciente: %s; tipoatend: %s; data: %s; usuário: %s",paciente.getNome(),tipoAtendimento.getDescricao(),  new DateTime().toString() ,loginController.getUserName()));
			// boolean possuiRegistro = false;
			for (Sessao s : sessoes) {
				if (s.getId() != null && s.getId() > 0) {
					// possuiRegistro = true;
					// sessaoService.editar(s);
					// Map<String, Object> params=new HashMap<String, Object>();
					// params.put("id", s.getId());
					// List<Sessao> sessao = sessaoService.buscar(params);
					// sessoesUpdate.add(sessao.get(0));
				} else {
					sessaoService.criar(s);
					// sessoesUpdate.add(s);
				}

			}

			atendimento.setSessoes(sessoes);
			// if (possuiRegistro)
			atendimentoService.editar(this.atendimento);

		} catch (Exception e) {
			addErrorMessage(ResultMessages.ERROR_CRUD.getDescricao()
					+ e.getLocalizedMessage());
			return;
		}
		init();

		addInfoMessage(ResultMessages.CREATE_SUCESS.getDescricao());
	}

	public void adicionarSessao() {
		if (atendimento.getQuantidade() == sessoes.size()) {
			addErrorMessage("A quantidade de Passes já foi atingida, não é possível adicionar mais itens.");
			return;
		}
		sessao.setSala(sala);
		sessoes.add(sessao);
		contadorPasses = sessoes.size();
		contadorPassesSala01=0;
		contadorPassesSala02=0;
		Map<Long, Integer> contadorSalasMap = new HashMap<Long, Integer>();
		int q1=contadorPassesSala01=atendimento.getQuantidadesala1();
		int q2=contadorPassesSala02=atendimento.getQuantidadesala2();
		
		for (Sessao s : sessoes) {
			
			long salaId=s.getSala().getId();
			if (contadorSalasMap.containsKey(salaId)) {
				Integer valor = contadorSalasMap.get(salaId);
				contadorSalasMap.remove(salaId);
				contadorSalasMap.put(salaId, --valor);
			} else {
				//int q1=atendimento.getQuantidadesala1();
				//int q2=atendimento.getQuantidadesala2();
				contadorSalasMap.put(salaId, salaId == 6290 ? --q1 : --q2);
			}
		}

		for (Long key : contadorSalasMap.keySet()) {
			//6290 id da sala 01
			//6291 id da sala 02
			if (key == 6290)
				contadorPassesSala01 = contadorSalasMap.get(key);
			else
				contadorPassesSala02 = contadorSalasMap.get(key);
		}
		sessao = new Sessao();
		setSala(null);
	}

	public void editarSessao() {
		System.out.println("");
		setSala(sessaoEditar.getSala());
		setSessao(sessaoEditar);
		sessaoEditar.setSala(null);
		contadorPasses = sessoes.size();
		contadorPassesSala01=0;
		contadorPassesSala02=0;
		
		Map<Long, Integer> contadorSalasMap = new HashMap<Long, Integer>();
		int q1=contadorPassesSala01=atendimento.getQuantidadesala1();
		int q2=contadorPassesSala02=atendimento.getQuantidadesala2();
		
		for (Sessao s : sessoes) {

			long salaId=s.getSala().getId();
			if (contadorSalasMap.containsKey(salaId)) {
				Integer valor = contadorSalasMap.get(salaId);
				contadorSalasMap.remove(salaId);
				contadorSalasMap.put(salaId, --valor);
			} else {
				//int q1=atendimento.getQuantidadesala1();
				//int q2=atendimento.getQuantidadesala2();
				contadorSalasMap.put(salaId, salaId == 6290 ? --q1 : --q2);
			}
		}

		for (Long key : contadorSalasMap.keySet()) {
			//6290 id da sala 01
			//6291 id da sala 02
			if (key == 6290)
				contadorPassesSala01 = contadorSalasMap.get(key);
			else
				contadorPassesSala02 = contadorSalasMap.get(key);
		}
		// sessoes.add(sessao);
		// sessao=new Sessao();
	}

	public void removerSessao() {
		System.out.println("");
		contadorPasses = sessoes.size();
		contadorPassesSala01=0;
		contadorPassesSala02=0;
		Map<Long, Integer> contadorSalasMap = new HashMap<Long, Integer>();
		int q1=contadorPassesSala01=atendimento.getQuantidadesala1();
		int q2=contadorPassesSala02=atendimento.getQuantidadesala2();

		for (Sessao s : sessoes) {

			long salaId=s.getSala().getId();
			if (contadorSalasMap.containsKey(salaId)) {
				Integer valor = contadorSalasMap.get(salaId);
				contadorSalasMap.remove(salaId);
				contadorSalasMap.put(salaId, --valor);
			} else {
				//int q1=atendimento.getQuantidadesala1();
				//int q2=atendimento.getQuantidadesala2();
				//6290 id da sala 01
				//6291 id da sala 02
				contadorSalasMap.put(salaId, salaId == 6290 ? --q1 : --q2);
			}
		}

		for (Long key : contadorSalasMap.keySet()) {
			//6290 id da sala 01
			//6291 id da sala 02
			if (key == 6290)
				contadorPassesSala01 = contadorSalasMap.get(key);
			else
				contadorPassesSala02 = contadorSalasMap.get(key);
		}
		// sessoes.remove(sessao);
		// sessao=new Sessao();
	}

	public void carregarPasses() {
		Map<Long, Integer> contadorSalasMap = new HashMap<Long, Integer>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("paciente.id", getPaciente().getId());
		List<Atendimento> list = atendimentoService.buscar(params);

		if (list != null && !list.isEmpty()) {
			sessoes.clear();
			sessoes.addAll(list.get(0).getSessoes());
			contadorPasses = sessoes.size();
			
			int q1=contadorPassesSala01=list.get(0).getQuantidadesala1();
			int q2=contadorPassesSala02=list.get(0).getQuantidadesala2();
			for (Sessao s : sessoes) {

				long salaId=s.getSala().getId();
				if (contadorSalasMap.containsKey(salaId)) {
					Integer valor = contadorSalasMap.get(salaId);
					contadorSalasMap.remove(salaId);
					contadorSalasMap.put(salaId, --valor);
				} else {
					//q1=list.get(0).getQuantidadesala1();
					//q2=list.get(0).getQuantidadesala2();
					//6290 id da sala 01
					//6291 id da sala 02
					contadorSalasMap.put(salaId, salaId == 6290 ? --q1 : --q2);
				}
			}

			for (Long key : contadorSalasMap.keySet()) {
				//6290 id da sala 01
				//6291 id da sala 02
				if (key == 6290)
					contadorPassesSala01 = contadorSalasMap.get(key);
				else
					contadorPassesSala02 = contadorSalasMap.get(key);
			}

			setTipoAtendimento(list.get(0).getTipoAtendimento());
			setAtendimento(list.get(0));
			setSala(null);
		} else {
			sessoes.clear();
			contadorPasses = 0;
			contadorPassesSala01 = 0;
			contadorPassesSala02 = 0;
			setTipoAtendimento(null);
			setSala(null);
			setAtendimento(new Atendimento());
			getAtendimento().setDataAtendimento(new Date());
		}
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

	public Atendimento getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(Atendimento atendimento) {
		this.atendimento = atendimento;
	}

	public AtendimentoService getAtendimentoService() {
		return atendimentoService;
	}

	public void setAtendimentoService(AtendimentoService atendimentoService) {
		this.atendimentoService = atendimentoService;
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

	public TipoAtendimentoService getTipoAtendimentoService() {
		return tipoAtendimentoService;
	}

	public void setTipoAtendimentoService(
			TipoAtendimentoService tipoAtendimentoService) {
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

	public boolean getEnableAutocomplete() {
		return enableAutocomplete;
	}

	public void setEnableAutocomplete(boolean enableAutocomplete) {
		this.enableAutocomplete = enableAutocomplete;
	}

	public List<Atendimento> getAtendimentos() {
		return atendimentos;
	}

	public void setAtendimentos(List<Atendimento> atendimentos) {
		this.atendimentos = atendimentos;
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

	public Sessao getSessao() {
		return sessao;
	}

	public void setSessao(Sessao sessao) {
		this.sessao = sessao;
	}

	public List<Sessao> getSessoes() {
		return sessoes;
	}

	public void setSessoes(List<Sessao> sessoes) {
		this.sessoes = sessoes;
	}

	public Sessao getSessaoEditar() {
		return sessaoEditar;
	}

	public void setSessaoEditar(Sessao sessaoEditar) {
		this.sessaoEditar = sessaoEditar;
	}

	public Sessao getSessaoRemover() {
		return sessaoRemover;
	}

	public void setSessaoRemover(Sessao sessaoRemover) {
		this.sessaoRemover = sessaoRemover;
	}

	public int getContadorPasses() {
		return contadorPasses;
	}

	public void setContadorPasses(int contadorPasses) {
		this.contadorPasses = contadorPasses;
	}

	public SessaoService getSessaoService() {
		return sessaoService;
	}

	public void setSessaoService(SessaoService sessaoService) {
		this.sessaoService = sessaoService;
	}

	public int getContadorPassesSala01() {
		return contadorPassesSala01;
	}

	public void setContadorPassesSala01(int contadorPassesSala01) {
		this.contadorPassesSala01 = contadorPassesSala01;
	}

	public int getContadorPassesSala02() {
		return contadorPassesSala02;
	}

	public void setContadorPassesSala02(int contadorPassesSala02) {
		this.contadorPassesSala02 = contadorPassesSala02;
	}

	public Atendimento getAtendimentoById(long id) {
		for (Atendimento uc : getAtendimentos()) {
			if (uc.getId() == id)
				return uc;
		}
		return null;
	}
}
