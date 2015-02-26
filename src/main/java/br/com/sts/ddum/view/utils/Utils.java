package br.com.sts.ddum.view.utils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import br.com.sts.ddum.view.controllers.BaseController;

public class Utils {

	/**
	 * Limpa os dados dos componentes de edição e de seus filhos,
	 * recursivamente. Checa se o componente é instância de EditableValueHolder
	 * e 'reseta' suas propriedades.
	 * <p>
	 * Quando este método, por algum motivo, não funcionar, parta para
	 * ignorância e limpe o componente assim:
	 * <p>
	 * <blockquote>
	 * 
	 * <pre>
	 * component.getChildren().clear()
	 * </pre>
	 * 
	 * </blockquote> :-)
	 *
	 * 
	 */
	public static void cleanSubmittedValues(UIComponent component) {
		if (component instanceof EditableValueHolder) {
			EditableValueHolder evh = (EditableValueHolder) component;
			evh.setSubmittedValue(null);
			evh.setValue(null);
			evh.setLocalValueSet(false);
			evh.setValid(true);
		}
		// Dependendo de como se implementa um Composite Component, ele retorna
		// ZERO
		// na busca por filhos. Nesse caso devemos iterar sobre os componentes
		// que o
		// compõe de forma diferente.
		if (UIComponent.isCompositeComponent(component)) {
			Iterator i = component.getFacetsAndChildren();
			while (i.hasNext()) {
				UIComponent comp = (UIComponent) i.next();

				// TODO: isolar em um método?
				if (comp.getChildCount() > 0) {
					for (UIComponent child : comp.getChildren()) {
						cleanSubmittedValues(child);
					}
				}
			}
		}
		// TODO: isolar em um método?
		if (component.getChildCount() > 0) {
			for (UIComponent child : component.getChildren()) {
				cleanSubmittedValues(child);
			}
		}
	}

	// converte o valor do cpf formatado para somente numeros em formato de
	// string
	public static String convertFormatCPF(String cpf) {
		return cpf.replace(".", "").replace("-", "").trim();
	}

	public static String pegarSeisCaracteres(String codElementoDespesa) {
		if (codElementoDespesa != null && codElementoDespesa.length() > 6)
			return codElementoDespesa.substring(0, 6);
		return codElementoDespesa;
	}

	public static BigDecimal convertStringToBigDecimal(String valorRepasse) {
		return new BigDecimal(valorRepasse.replace("R$", "").replace(".", "")
				.replace(",", ".").trim());
	}

	public static <T extends BaseController> T getControllerInstance(
			Class<T> klass) {
		FacesContext currentInstance = FacesContext.getCurrentInstance();

		T newInstance = klass.cast(currentInstance
				.getELContext()
				.getELResolver()
				.getValue(
						currentInstance.getELContext(),
						null,
						klass.getSimpleName().substring(0, 1).toLowerCase()
								+ klass.getSimpleName().substring(1)));

		return newInstance;
	}

	public static boolean possuiValorValido(Object... valores) {
		boolean result = true;
		for (Object object : valores) {
			if (object instanceof Collection) {
				if (((Collection) object).isEmpty())
					result = false;
			} else if (object == null || object.equals(""))
				result = false;
			result &= result;
		}
		return result;
	}
}
