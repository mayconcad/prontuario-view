package br.com.saude.prontuario.view.controllers;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.omnifaces.util.Ajax;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.component.media.Media;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import br.com.saude.prontuario.model.entities.Documento;
import br.com.saude.prontuario.model.entities.PrestacaoConta;
import br.com.saude.prontuario.model.entities.Repasse;
import br.com.saude.prontuario.model.entities.Responsavel;
import br.com.saude.prontuario.model.enums.ResultMessages;
import br.com.saude.prontuario.model.springsecurity.entities.User;
import br.com.saude.prontuario.model.utils.UtilsModel;
import br.com.saude.prontuario.service.interfaces.ConnectionConfigService;
import br.com.saude.prontuario.service.interfaces.PrestacaoContaService;
import br.com.saude.prontuario.service.interfaces.RepasseService;
import br.com.saude.prontuario.service.interfaces.ResponsavelService;
import br.com.saude.prontuario.service.interfaces.UserService;
import br.com.saude.prontuario.view.uploads.UploadedFileUtil;
import br.com.saude.prontuario.view.utils.UtilsView;

/**
 * @author developer
 *
 */
/**
 * @author developer
 *
 */
@Controller
@ViewScoped
public class PrestacaoContaController extends BaseReportController {

	private static final long serialVersionUID = -3922297442654647722L;

	@Autowired
	private UserService userService;

	@Autowired
	private RepasseService repasseService;

	@Autowired
	private ResponsavelService responsavelService;

	@Autowired
	private PrestacaoContaService prestacaoContaService;

	@Autowired
	private ConnectionConfigService connectionConfigService;

	private PrestacaoConta prestacaoConta;

	private Date dataPrestacao;

	private String saldoAberto;

	private String totalRepasse;

	private String valorDoc;

	private String valorTotal;

	private UploadedFile arquivoUploadFile;

	private InputText arquivoNomeFileUpload;

	private Media prestacaoContaMedia;

	private Documento documento;
	private Documento documentoEdite;
	private Documento documentoRemove;

	private List<Documento> documentos;

	private Statement createStatement;
	private Connection conexaoBancoCGP;

	private Repasse repasse;
	private Repasse repasseProximo;

	private StreamedContent reportStream;

	@PostConstruct
	public void init() {
		prestacaoContaMedia = new Media();
		dataPrestacao = new Date();
		arquivoUploadFile = new UploadedFileUtil();
		prestacaoConta = new PrestacaoConta();
		repasse = repasseProximo = new Repasse();
		if (repasse.getValorRepasse() == null
				|| repasse.getValorRepasse().compareTo(BigDecimal.ZERO) == 0) {
			totalRepasse = saldoAberto = "R$ 0,00";
		} else {
			totalRepasse = saldoAberto = UtilsModel
					.convertBigDecimalToString(repasse.getValorRepasse());
		}
		documento = documentoEdite = documentoRemove = new Documento();
		documentos = new ArrayList<Documento>();
		arquivoNomeFileUpload = new InputText();
		arquivoNomeFileUpload.setStyleClass(null);
		valorTotal = "R$ 0,00";
		valorDoc = new String();
		conexaoBancoCGP = connectionConfigService.obterConexaoBancoCGP();
		Ajax.update(":prestacaoContaTabView");
	}

	public void setRepasseSemPrestacaoConta() {

		// if (true) {
		// addErrorMessage("Esta funcionalidade ainda não está disponível!");
		// return;
		// }

		this.init();
		LoginBean controllerInstance = UtilsView
				.getControllerInstance(LoginBean.class);
		User currentUser = (User) controllerInstance.getCurrentUser();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("user.id", currentUser.getId());
		List<Responsavel> responsaveis = responsavelService.buscar(params);
		if (responsaveis == null || responsaveis.isEmpty()) {
			addErrorMessage("A operação não pode ser realizada, pois o usuário não é um Responsável!");
			return;
		}

		Responsavel responsavel = responsaveis.get(0);

		params.clear();
		if (responsavel != null)
			params.put("unidade.responsavel.id", responsavel.getId());
		List<Repasse> repasses = repasseService.buscar(params);
		if (repasses == null || repasses.isEmpty()) {
			addErrorMessage("A operação não pode ser realizada, não existe Repasse para este Responsável!");
			return;
		}

		for (Repasse repasseItem : repasses) {
			params.clear();
			params.put("repasse.id", repasseItem.getId());
			if (prestacaoContaService.buscar(params).isEmpty()) {
				this.repasse = repasseItem;
				totalRepasse = saldoAberto = UtilsModel
						.convertBigDecimalToString(this.repasse
								.getValorRepasse());
				break;
			}
		}
		if (repasse.getId() == null || repasse.getId() == 0l) {
			addErrorMessage("Não existe Repasse Ativo para este Responsável!");
			return;
		}
	}

	public void registrar() {

		// if (true) {
		// addErrorMessage("Esta funcionalidade ainda não está disponível!");
		// return;
		// }

		if (!UtilsModel.possuiValorValido(getDocumentos())) {
			addErrorMessage("Pelo menos um documento deve ser informado!");
			return;
		}

		List<Documento> asList = new ArrayList<Documento>(getDocumentos());
		prestacaoConta.setDocumentos(asList);
		try {
			prestacaoConta.setSaldoDisponivel(UtilsModel
					.convertStringToBigDecimal(getSaldoAberto()));
			prestacaoConta.setRepasse(repasse);
			// registro de uma prestação de contas
			prestacaoContaService.salvar(prestacaoConta);

			Repasse proxRepasse = new Repasse();
			proxRepasse.setAtivo(repasse.isAtivo());
			proxRepasse.setCodAplicacao(repasse.getCodAplicacao());
			proxRepasse.setDataEmissao(repasse.getDataEmissao());
			proxRepasse.setExercicio(repasse.getExercicio());
			proxRepasse.setHistorico(repasse.getHistorico());
			proxRepasse.setNaturezaEmpenho(repasse.getNaturezaEmpenho());
			proxRepasse.setNumeroEmpenho(repasse.getNumeroEmpenho());
			proxRepasse.setNumeroProcesso(repasse.getNumeroProcesso());
			proxRepasse.setTipoCredito(repasse.getTipoCredito());
			proxRepasse.setTipoEmpenho(repasse.getTipoEmpenho());
			proxRepasse.setTipoMeta(repasse.getTipoMeta());
			proxRepasse.setUnidade(repasse.getUnidade());
			proxRepasse.setValorEmpenho(repasse.getValorEmpenho());
			proxRepasse.setValorRepasse(repasse.getValorRepasse());

			// criar um repasse - empenho - liquidacao
			repasseService.repasseAutomatico(proxRepasse, proxRepasse
					.getUnidade().getResponsavel(), prestacaoConta
					.getSaldoDisponivel(), repasseService
					.obterProximoNumeroEmpenho(proxRepasse.getUnidade()
							.getUnidadeContabil()), false);
			generateReportStream(
					UtilsModel.convertStringToBigDecimal(valorTotal),
					dataPrestacao, proxRepasse.getUnidade().getResponsavel());

			getPrestacaoContaMedia().setValue("/reports/PrestacaoContas.pdf");

			carregarReportTab();

		} catch (Exception e) {
			prestacaoContaService.remover(prestacaoConta);
			addErrorMessage(ResultMessages.ERROR_CRUD.getDescricao()
					+ e.getLocalizedMessage());
			return;
		}
		addInfoMessage(ResultMessages.CREATE_SUCESS.getDescricao());
		// AQUI DEVE-SE CARREGAR VALORES DE OUTRO REPASSE CASO O RESPONSÁVEL
		// TENHA
		setRepasseSemPrestacaoConta();
		// totalRepasse = saldoAberto = "R$ 0,00";
		// valorTotal = "R$ 0,00";
		// init();
	}

	public void uploadDoArquivoEvent(FileUploadEvent event) {
		arquivoNomeFileUpload.setStyleClass(null);
		// if (event.getFile().getSize() > 3072000) {
		// arquivoNomeFileUpload.setValid(false);
		// addErrorMessage("Tamanho Máximo do Arquivo é de 3 MB");
		// return;
		// }
		this.arquivoUploadFile = event.getFile();
	}

	public void adicionarArquivo() {
		arquivoNomeFileUpload.setStyleClass(null);
		if (this.arquivoUploadFile == null
				|| this.arquivoUploadFile.getFileName() == null) {
			addErrorMessage("Pelo menos um arquivo de tipos (pdf,png,jpeg ou jpg) deve ser informado");
			arquivoNomeFileUpload.setValid(false);
			return;
		}

		if (documento.getNumero() == null || "".equals(documento.getNumero()))
			documento.setNumero(gerarNumeroIncremental());
		documento.setTamanho(arquivoUploadFile.getSize());
		String caminho = FacesContext.getCurrentInstance().getExternalContext()
				.getRealPath(File.separator);
		documento.setCaminho(caminho);
		documento.setArquivo(arquivoUploadFile.getContents());
		documento.setNome(arquivoUploadFile.getFileName());

		documento.setContentType(arquivoUploadFile.getContentType());
		documento.setAtivo(true);
		documento.setNomeOriginal(String.format("%s%s%s", caminho,
				File.separator, arquivoUploadFile.getFileName()));
		documento.setValor(UtilsModel.convertStringToBigDecimal(getValorDoc()));

		BigDecimal saldoBigDec = UtilsModel.convertStringToBigDecimal(
				saldoAberto).subtract(documento.getValor());
		saldoAberto = UtilsModel.convertBigDecimalToString(saldoBigDec);
		if (saldoBigDec.compareTo(BigDecimal.ZERO) < 0) {
			addErrorMessage("Saldo insuficiente para o valor informado!");
			saldoAberto = UtilsModel.convertBigDecimalToString(saldoBigDec
					.add(documento.getValor()));
			return;
		}

		valorTotal = UtilsModel.convertBigDecimalToString(UtilsModel
				.convertStringToBigDecimal(valorTotal)
				.add(documento.getValor()));
		documentos.add(documento);
		documento = new Documento();
		arquivoUploadFile = new UploadedFileUtil();
		valorDoc = new String();
	}

	/**
	 * @description gera um número incremental par ao documento do qual o número
	 *              não foi informado, deve-se seguir o padrão diames-001 e
	 *              assim incrementando
	 * @return String
	 */
	private String gerarNumeroIncremental() {
		Calendar date = Calendar.getInstance();
		date.setTime(new Date());

		if (!documentos.isEmpty()) {
			ArrayList<String> numeros = new ArrayList<String>();
			for (Documento documento : documentos) {
				String numeroIncrem = documento.getNumero().split("-")[1];
				if (!"".equals(numeroIncrem))
					numeros.add(numeroIncrem);
			}
			Collections.sort(numeros);
			Integer maiorNumero = Integer
					.parseInt(numeros.get(numeros.size() - 1));
			maiorNumero += maiorNumero;
			return String.format("%s%s - 00%d",
					date.get(Calendar.DAY_OF_MONTH),
					date.get(Calendar.MONTH) + 1, maiorNumero);
		} else
			return String.format("%s%s-001", date.get(Calendar.DAY_OF_MONTH),
					date.get(Calendar.MONTH) + 1);
	}

	public void editarDoc() {
		setDocumento(documentoEdite);
		valorTotal = UtilsModel.convertBigDecimalToString(UtilsModel
				.convertStringToBigDecimal(valorTotal).subtract(
						documentoEdite.getValor()));
		saldoAberto = UtilsModel.convertBigDecimalToString(UtilsModel
				.convertStringToBigDecimal(saldoAberto).add(
						documentoEdite.getValor()));
		setValorDoc(converterForCurrency(documentoEdite.getValor().toString()));
		arquivoUploadFile = new UploadedFileUtil(documentoEdite.getTamanho(),
				documentoEdite.getNome(), null, documentoEdite.getArquivo(),
				documentoEdite.getTipo());
	}

	public void removerDoc() {
		valorTotal = UtilsModel.convertBigDecimalToString(UtilsModel
				.convertStringToBigDecimal(valorTotal).subtract(
						documentoRemove.getValor()));
		saldoAberto = UtilsModel.convertBigDecimalToString(UtilsModel
				.convertStringToBigDecimal(saldoAberto).add(
						documentoRemove.getValor()));
	}

	public void generateReportStream(BigDecimal valor, Date dataPrestacao,
			Responsavel credor) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("valor", UtilsModel.convertBigDecimalToString(valor));
		parameters.put("dataPrestacao", sdf.format(dataPrestacao));
		parameters.put(
				"agencia",
				String.format("%s-%s", credor.getNumeroAgencia(),
						credor.getDigitoConta()));
		parameters.put(
				"conta",
				String.format("%s-%s", credor.getNumeroConta(),
						credor.getDigitoAgencia()));
		parameters.put("local", "Parnaíba - PI");

		FacesContext context = FacesContext.getCurrentInstance();
		ServletContext servletContext = (ServletContext) context
				.getExternalContext().getContext();

		generateStremedReport(servletContext.getRealPath(File.separator.concat(
				"reports").concat(File.separator)), "PrestacaoContas",
				new ArrayList(), parameters);
	}

	private String converterForCurrency(String valor) {
		return valor = valor.replace("R$", "").replace(".", ",").trim();
	}

	public List<Repasse> autocompleteRepasse(String valor) {
		return repasseService.autocompletar(valor);
	}

	public void carregarReportTab() {
		getEditTab().setRendered(true);
		TabView parent = (TabView) getEditTab().getParent();
		int findIndex = parent.getChildren().indexOf(getEditTab());
		parent.setActiveIndex(findIndex);
	}

	public void limparRelatorio() {
		getPrestacaoContaMedia().setValue(null);
	}

	public PrestacaoContaService getPrestacaoContaService() {
		return prestacaoContaService;
	}

	public void setPrestacaoContaService(
			PrestacaoContaService prestacaoContaService) {
		this.prestacaoContaService = prestacaoContaService;
	}

	public ResponsavelService getResponsavelService() {
		return responsavelService;
	}

	public void setResponsavelService(ResponsavelService responsavelService) {
		this.responsavelService = responsavelService;
	}

	public RepasseService getRepasseService() {
		return repasseService;
	}

	public void setRepasseService(RepasseService repasseService) {
		this.repasseService = repasseService;
	}

	public Date getDataPrestacao() {
		return dataPrestacao;
	}

	public void setDataPrestacao(Date dataPrestacao) {
		this.dataPrestacao = dataPrestacao;
	}

	public String getSaldoAberto() {
		return saldoAberto;
	}

	public void setSaldoAberto(String saldoAberto) {
		this.saldoAberto = saldoAberto;
	}

	public UploadedFile getArquivoUploadFile() {
		return arquivoUploadFile;
	}

	public void setArquivoUploadFile(UploadedFile arquivoUploadFile) {
		this.arquivoUploadFile = arquivoUploadFile;
	}

	public InputText getArquivoNomeFileUpload() {
		return arquivoNomeFileUpload;
	}

	public void setArquivoNomeFileUpload(InputText arquivoNomeFileUpload) {
		this.arquivoNomeFileUpload = arquivoNomeFileUpload;
	}

	public Documento getDocumento() {
		return documento;
	}

	public void setDocumento(Documento documento) {
		this.documento = documento;
	}

	public List<Documento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<Documento> documentos) {
		this.documentos = documentos;
	}

	public PrestacaoConta getPrestacaoConta() {
		return prestacaoConta;
	}

	public void setPrestacaoConta(PrestacaoConta prestacaoConta) {
		this.prestacaoConta = prestacaoConta;
	}

	public Documento getDocumentoEdite() {
		return documentoEdite;
	}

	public void setDocumentoEdite(Documento documentoEdite) {
		this.documentoEdite = documentoEdite;
	}

	public Documento getDocumentoRemove() {
		return documentoRemove;
	}

	public void setDocumentoRemove(Documento documentoRemove) {
		this.documentoRemove = documentoRemove;
	}

	public String getTotalRepasse() {
		return totalRepasse;
	}

	public void setTotalRepasse(String totalRepasse) {
		this.totalRepasse = totalRepasse;
	}

	public String getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(String valorTotal) {
		this.valorTotal = valorTotal;
	}

	public String getValorDoc() {
		return valorDoc;
	}

	public void setValorDoc(String valorDoc) {
		this.valorDoc = valorDoc;
	}

	public Repasse getRepasse() {
		return repasse;
	}

	public void setRepasse(Repasse repasse) {
		this.repasse = repasse;
	}

	public Repasse getRepasseProximo() {
		return repasseProximo;
	}

	public void setRepasseProximo(Repasse repasseProximo) {
		this.repasseProximo = repasseProximo;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public StreamedContent getReportStream() {
		return reportStream;
	}

	public void setReportStream(StreamedContent reportStream) {
		this.reportStream = reportStream;
	}

	public Media getPrestacaoContaMedia() {
		return prestacaoContaMedia;
	}

	public void setPrestacaoContaMedia(Media prestacaoContaMedia) {
		this.prestacaoContaMedia = prestacaoContaMedia;
	}
}
