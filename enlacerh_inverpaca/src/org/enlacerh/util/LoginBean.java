package org.enlacerh.util;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.primefaces.context.RequestContext;


@ManagedBean
@ViewScoped
public class LoginBean extends Utils {


	private String usuario;
    private String clave;
    FacesMessage msj = null;
    HttpSession sesionOk;
    private String sessionId;
    StringMD md = new StringMD(); //Objeto encriptador
    HttpServletRequest rq = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	String URL = rq.getRequestURL().toString();
	String logged = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("nbruser");

      

    public String getUsuario ()
    {
        return usuario;
    }


    public void setUsuario (final String usuario)
    {
        this.usuario = usuario.toUpperCase();
    }


    public String getClave ()
    {
        return clave;
    }


    public void setClave (final String clave)
    {
        this.clave = clave.toUpperCase();
    }
    


	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}


	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	/**
	 * Leer Datos de Usuarios
	 * <p>
	 * Se conecta a la base de datos y valida el usuario y la contrase�a.
	 * Adicionalmente valida si el usuario existe ne la base de datos.
	 * @throws IOException 
	 * @return String
	 * **/
	public void login() throws NamingException, IOException {
		
		Seg003forLogin seg = new Seg003forLogin(); // Crea objeto para el login
		String[][] tabla = null;
		int rows = 0;
		// LLama al m�todo que retorna el usuario y la contrase�a
		seg.selectLogin(usuario, JNDI, "autos01");
		tabla = seg.getArray();
		rows = seg.getRows();
		
		if (rows == 0) {
			msj = new FacesMessage(FacesMessage.SEVERITY_ERROR, getMessage("noreg"), "");
			FacesContext.getCurrentInstance().addMessage(null, msj);
		 usuario = "";
		}
		
		//Se asignan a dos variables String ya que retorna un arreglo y debe convertirse a String
		// y se convierte en may�scula
		if(rows>0){
		String vLusuario = tabla[0][0].toUpperCase().toString();
		String vLclave = tabla[0][1].toString();
		//System.out.println("Usuario: " + vLusuario);
		//System.out.println("Clave: " + vLclave);
		//System.out.println("clave:" + md.getStringMessageDigest(clave,StringMD.SHA256));
		//System.out.println("Vlclave: " + vLclave);
		
		//Valida que usuario y claves sean los mismos, realiza el login y crea la variable de session
		if(usuario.equals(vLusuario) && !md.getStringMessageDigest(clave,StringMD.SHA256).equals(vLclave)){
			msj = new FacesMessage(FacesMessage.SEVERITY_ERROR, getMessage("logCl"), "");
			FacesContext.getCurrentInstance().addMessage(null, msj);
		} else	if(usuario.equals(vLusuario) && md.getStringMessageDigest(clave,StringMD.SHA256).equals(vLclave)){
			//System.out.println("Usuario y contrase�a correctos");
			//Creando la variable de session	
			sesionOk = ( HttpSession ) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
			//sesionOk.setAttribute("usuario", usuario);
			//System.out.println("URL: " + URL);
			//sesionOk.setMaxInactiveInterval(getSession());
			sessionId = sesionOk.getId();
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", usuario);
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("session", sessionId);
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("cedula", tabla[0][3]);
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("grupo", tabla[0][5]);
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("cia", tabla[0][4]);
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("nbruser", tabla[0][6]);
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("admin", tabla[0][7]);
			FacesContext.getCurrentInstance().getExternalContext().redirect("/enlacerh/jsf/enlacerh.xhtml"); 
		} 
		
		}
		//}
		//System.out.println("Retorno: " + login);
        
	}
	
	
    /**
     * Retorna el nombre de la sesi�n activa.
     * Si la sesi�n es null,
     * entonces llama al m�todo logout que la invalida
     * redirecciona al logout, retornando blanco.
     * @return String
     */ 
	public String getLogged() throws IOException {
		//System.out.println(rq.isRequestedSessionIdValid());
		
		if (!rq.isRequestedSessionIdValid()) {
			//rq.getCurrentInstance().execute("PF('yourdialogid').show()");
			RequestContext.getCurrentInstance().execute("PF('idleDialog').show()");
			return null;
		} else {
		
	    return logged.toLowerCase();
		}		
	}

    
    /**
     * Invalida la session y sale de la aplicaci�n
     * @return String
     */ 
    public String logout() throws IOException{
    	//Invalida la session
    	FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
    	//Redirecciona la p�gina
    	FacesContext.getCurrentInstance().getExternalContext().redirect("/enlacerh/index.xhtml"); 
    	//FacesContext.getCurrentInstance().getExternalContext().redirect("https://www.enlacerh.com/"); 
    	logged = null;    	return "";
    	
 	}
    
    public void isLogged() throws IOException{
    	//Redirecciona la p�gina
    	//System.out.println("Epa: " + logged);
    	if (logged!=null) {
    		FacesContext.getCurrentInstance().getExternalContext().redirect("/enlacerh/jsf/enlacerh.xhtml"); 
    		//System.out.println("Ya se loge�, redireccionar");
        }
    	//FacesContext.getCurrentInstance().getExternalContext().redirect("/ri/faces/jsf/index.xhtml"); 
    	}
    
 
  

}
