package net.xaviersala.lluitadors;

import net.xaviersala.lluitadors.lluita.Lluitador;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.Header;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlunit.builder.Input;
import org.xmlunit.input.WhitespaceStrippedSource;
import org.xmlunit.matchers.CompareMatcher;
import spark.Spark;
import spark.utils.IOUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static spark.Spark.awaitInitialization;

/**
 * Comprovació del funcionament de l'aplicació web des
 * d'un punt de vista funcional.
 *
 * Created by xavier on 13/12/16.
 */
public class AppTest {

    private static final String MAGIC = "Magic";
    private static final String MAGIC2 = "Magic2";
    private static final String LA_RESPOSTA_NO_HAURIA_DE_SER_NULL = "La resposta no hauria de ser null";
    private List<NameValuePair> formParams;
    private HttpClient client;

    private static final String SERVER_URL = "http://localhost:8080";

    @BeforeClass
    public static void beforeClass() throws Exception {
        App.main(null);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        Spark.stop();
    }

    @Before
    public void setUp() throws Exception {
        awaitInitialization();
        client = HttpClientBuilder.create().disableRedirectHandling().build();
        formParams = new ArrayList<>();
    }

    /**
     * Comprova que es carrega l'índex.
     */
    @Test
    public void comprovaIndex() {
        TestResponse res = peticio("GET", "", null);
        if (res!=null) {
            assertEquals(200, res.status);
        } else {
            fail(LA_RESPOSTA_NO_HAURIA_DE_SER_NULL);
        }

    }

    /**
     * Comprova que al accedir a /Lluitadors el sistema es redirigeix
     * a l'arrel.
     */
    @Test
    public void comprovaQueEsRedirigeixALArrel() {
        TestResponse res = peticio("GET", "/Lluitadors", null);

        assertNotNull(res);
        assertEquals(302, res.status);
        assertEquals(SERVER_URL + "/", res.getRedirects());

    }

    /*
     *    *** LLISTA ***
     *  -----------------------------------------
     */

    /**
     * Comprova que al esborrar els Lluitadors realment s'esborren.
     */
    @Test
    public void comprovaQueEsBuidaLaLlistaAlBuidar() {

        TestResponse res = buidaCampionat();
        assertEquals(200, res.status);
        assertEquals(App.EXPULSATS_TOTS_ELS_LLUITADORS_DEL_CAMPIONAT, res.content());

        res = peticio("GET", App.LLUITADORS_LLISTA, null);
        if (res == null) {
            fail(LA_RESPOSTA_NO_HAURIA_DE_SER_NULL);
        }
        assertEquals(200, res.status);
        assertThat("<lluitadors/>",
                CompareMatcher.isIdenticalTo(new WhitespaceStrippedSource(Input.from(res.content()).build())));

    }

    @Test
    public void comprovaQueSurtLaLlistaDeLluitadors() {
        buidaCampionat();
        afegirLluitador(MAGIC);
        afegirLluitador(MAGIC2);

        TestResponse res = peticio("GET", App.LLUITADORS_LLISTA, null);
        if (res == null) {
            fail(LA_RESPOSTA_NO_HAURIA_DE_SER_NULL);
        }

        List<Lluitador> lluitadors = Arrays.asList(new Lluitador(MAGIC,1), new Lluitador(MAGIC2,2));
        assertThat(generaLlista(lluitadors),
                CompareMatcher.isIdenticalTo(new WhitespaceStrippedSource(Input.from(res.content()).build())));
    }


    /**
     *
     */
    @Test
    public void comprovaQueNoSAfegeixenJugadorsEnBlancNiNull() {
        TestResponse res = afegirLluitador("");
        assertEquals(400, res.status);
        assertEquals(App.ERROR_AL_AFEGIR_EL_LLUITADOR, res.content());

        res = afegirLluitador(null);
        assertEquals(400, res.status);
        assertEquals(App.ERROR_AL_AFEGIR_EL_LLUITADOR, res.content());

    }

    @Test
    public void comprovaQueSAfegeixenJugadors() {
        buidaCampionat();

        TestResponse res = afegirLluitador(MAGIC);
        assertEquals(302, res.status);
        assertEquals(SERVER_URL + App.LLUITADORS_LLISTA + "/" +  MAGIC, res.getRedirects());

        res = peticio("GET", App.LLUITADORS_LLISTA + "/" +  MAGIC, null);
        if (res == null) {
            fail(LA_RESPOSTA_NO_HAURIA_DE_SER_NULL);
        }
        assertEquals(200, res.status);
        assertThat(generaLluitadorXML(MAGIC),
                CompareMatcher.isIdenticalTo(new WhitespaceStrippedSource(Input.from(res.content()).build())));


        res = afegirLluitador(MAGIC2);
        assertEquals(302, res.status);
        assertEquals(SERVER_URL + App.LLUITADORS_LLISTA + "/" +  MAGIC2, res.getRedirects());

        res = peticio("GET", App.LLUITADORS_LLISTA + "/" +  MAGIC2, null);
        if (res == null) {
            fail(LA_RESPOSTA_NO_HAURIA_DE_SER_NULL);
        }
        assertEquals(200, res.status);
        assertThat(generaLluitadorXML(MAGIC2),
                CompareMatcher.isIdenticalTo(new WhitespaceStrippedSource(Input.from(res.content()).build())));
    }

    @Test
    public void comprovaQueNoSAfegeixUnJugadorDosCops() {

        buidaCampionat();
        afegirLluitador(MAGIC);

        TestResponse res = afegirLluitador(MAGIC);
        if (res == null) {
            fail(LA_RESPOSTA_NO_HAURIA_DE_SER_NULL);
        }

        assertEquals(400, res.status);
        assertEquals(Controlador.EL_LLUITADOR_JA_HI_ES + MAGIC, res.content());
    }

    /**
     *  *** LLUITA ***
     *  -----------------------------------------
     */

    @Test
    public void comprovaQueNoEsPotLluitarAmbSiMateix() {

        formParams.add(new BasicNameValuePair("nom1", "Bongo"));
        formParams.add(new BasicNameValuePair("nom2", "Bongo"));

        TestResponse res = peticio("POST", App.LLUITADORS_RING_LLUITA, formParams);
        if (res == null) {
            fail(LA_RESPOSTA_NO_HAURIA_DE_SER_NULL);
        }
        assertEquals(400, res.status);
        assertEquals(App.UN_LLUITADOR_NO_POT_LLUITAR_AMB_SI_MATEIX, res.content());
    }

    @Test
    public void comprovaQueSiFaltenParametresFalla() {
        buidaCampionat();
        afegirLluitador(MAGIC);
        afegirLluitador(MAGIC2);

        formParams.add(new BasicNameValuePair("nom1", MAGIC));
        TestResponse res = peticio("POST", App.LLUITADORS_RING_LLUITA, formParams);
        if (res == null) {
            fail(LA_RESPOSTA_NO_HAURIA_DE_SER_NULL);
        }
        assertEquals(400, res.status);
        assertEquals(App.ERROR_EN_ELS_LLUITADORS, res.content());
    }

    @Test
    public void comprovaQueNoEsPotLluitarAmbElPrimerJugadorInexistent() {
        buidaCampionat();

        formParams.add(new BasicNameValuePair("nom1", MAGIC));
        formParams.add(new BasicNameValuePair("nom2", MAGIC2));

        TestResponse res = peticio("POST", App.LLUITADORS_RING_LLUITA, formParams);
        if (res == null) {
            fail(LA_RESPOSTA_NO_HAURIA_DE_SER_NULL);
        }
        assertEquals(400, res.status);
        assertEquals(Controlador.NO_EXISTEIX_EL_LLUITADOR + MAGIC, res.content());
    }

    @Test
    public void comprovaQueNoEsPotLluitarAmbElSegonJugadorInexistent() {
        buidaCampionat();
        afegirLluitador(MAGIC);

        formParams.add(new BasicNameValuePair("nom1", MAGIC));
        formParams.add(new BasicNameValuePair("nom2", MAGIC2));

        TestResponse res = peticio("POST", App.LLUITADORS_RING_LLUITA, formParams);
        if (res == null) {
            fail(LA_RESPOSTA_NO_HAURIA_DE_SER_NULL);
        }
        assertEquals(400, res.status);
        assertEquals(Controlador.NO_EXISTEIX_EL_LLUITADOR + MAGIC2, res.content());
    }

    @Test
    public void comprovaQueGuanyaElPrimer() {
        buidaCampionat();
        afegirLluitador(MAGIC, 8);
        afegirLluitador(MAGIC2, 5);

        TestResponse res = lluita(MAGIC, MAGIC2);
        assertEquals(200, res.status);
        assertThat(generaResultatXML(MAGIC,MAGIC2),
                CompareMatcher.isIdenticalTo(new WhitespaceStrippedSource(Input.from(res.content()).build())));

        // Comprova que ha sumat a l'estadística ...
        res = peticio("GET", App.LLUITADORS_LLISTA, null);
        if (res == null) {
            fail(LA_RESPOSTA_NO_HAURIA_DE_SER_NULL);
        }

        List<Lluitador> lluitadors = new ArrayList<>(2);
        Lluitador ll = new Lluitador(MAGIC, 5);
        ll.setVictories(1);
        lluitadors.add(ll);
        ll = new Lluitador(MAGIC2, 5);
        ll.setDerrotes(1);
        lluitadors.add(ll);

        assertThat(generaLlista(lluitadors),
                CompareMatcher.isIdenticalTo(new WhitespaceStrippedSource(Input.from(res.content()).build())));
    }

    @Test
    public void comprovaQueUnCombatElPotGuanyarElVisitant() {
        buidaCampionat();
        afegirLluitador(MAGIC, 8);
        afegirLluitador(MAGIC2, 9);

        TestResponse res = lluita(MAGIC, MAGIC2);
        assertEquals(200, res.status);
        assertThat(generaResultatXML(MAGIC2,MAGIC),
                CompareMatcher.isIdenticalTo(new WhitespaceStrippedSource(Input.from(res.content()).build())));

        // Comprova que ha sumat a l'estadística ...
        res = peticio("GET", App.LLUITADORS_LLISTA, null);
        if (res == null) {
            fail(LA_RESPOSTA_NO_HAURIA_DE_SER_NULL);
        }

        List<Lluitador> lluitadors = new ArrayList<>(2);
        Lluitador ll = new Lluitador(MAGIC, 8);
        ll.setDerrotes(1);
        lluitadors.add(ll);
        ll = new Lluitador(MAGIC2, 9);
        ll.setVictories(1);
        lluitadors.add(ll);

        assertThat(generaLlista(lluitadors),
                CompareMatcher.isIdenticalTo(new WhitespaceStrippedSource(Input.from(res.content()).build())));
    }

    @Test
    public void comprovaQueDosLluitadorsPodenEmpatar() {
        buidaCampionat();
        afegirLluitador(MAGIC, 5);
        afegirLluitador(MAGIC2, 5);

        TestResponse res = lluita(MAGIC, MAGIC2);
        assertEquals(200, res.status);
        assertThat("<resultat/>",
                CompareMatcher.isIdenticalTo(new WhitespaceStrippedSource(Input.from(res.content()).build())));

        // Comprova que ha sumat a l'estadística ...
        res = peticio("GET", App.LLUITADORS_LLISTA, null);
        if (res == null) {
            fail(LA_RESPOSTA_NO_HAURIA_DE_SER_NULL);
        }

        // Mirem si ha quedat registrat l'empat
        List<Lluitador> lluitadors = new ArrayList<>(2);
        Lluitador ll = new Lluitador(MAGIC, 5);
        ll.setEmpats(1);
        lluitadors.add(ll);
        ll = new Lluitador(MAGIC2, 5);
        ll.setEmpats(1);
        lluitadors.add(ll);

        assertThat(generaLlista(lluitadors),
                CompareMatcher.isIdenticalTo(new WhitespaceStrippedSource(Input.from(res.content()).build())));
    }

    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------
    // --------------------------------------------------------------------------------------

    /**
     * Afegir un lluitador definint quina és la seva forca.
     *
     * @param qui lluitador
     * @param forca força
     * @return connexió
     */
    private TestResponse afegirLluitador(String qui, int forca) {
        formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("nom", qui));
        formParams.add(new BasicNameValuePair("forca", String.valueOf(forca)));
        return peticio("POST", App.LLUITADORS_RING_ADD, formParams);
    }

    /**
     * Afegeix un lluitador al campionat.
     * @param qui nom
     * @return resultat de la petició
     */
    private TestResponse afegirLluitador(String qui) {
        formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("nom", qui));
        return peticio("POST", App.LLUITADORS_LLISTA, formParams);
    }

    /**
     * Genera el resultat XML d'un lluitador.
     * @param nom lluitador
     * @return cadena XML
     */
    private String generaLluitadorXML(String nom) {
        return "<lluitador>" +
                "<nom>"+nom+"</nom>" +
                "<victories>0</victories>" +
                "<derrotes>0</derrotes>" +
                "<empats>0</empats>" +
                "</lluitador>";
    }

    /**
     * Genera l'XML amb el resultat de la lluita entre magic(guanyador) i magic2(derrotat).
     * @param magic nom del guanyador
     * @param magic2 nom del derrotat
     * @return XML amb el resultat
     */
    private String generaResultatXML(String magic, String magic2) {
        return "<resultat>" +
                "<victoria>" + magic + "</victoria>" +
                "<derrota>" + magic2 + "</derrota>" +
                "</resultat>";
    }


    /**
     * Genera la llista XML de lluitadors i les estadístiques
     * @param lluitadors lluitadors a llistar
     * @return XML amb la llista de lluitadors
     */
    private String generaLlista(List<Lluitador> lluitadors) {
        String resultat = "<lluitadors>";
        for(Lluitador l: lluitadors) {
            resultat += "<lluitador>";
            resultat += "<nom>" + l.getNom() + "</nom>";
            resultat += "<victories>" + l.getVictories() + "</victories>";
            resultat += "<derrotes>" + l.getDerrotes() + "</derrotes>";
            resultat += "<empats>" + l.getEmpats() + "</empats>";
            resultat += "</lluitador>";
        }
        resultat += "</lluitadors>";
        return resultat;
    }

    /**
     * Buida el campionat de lluitadors.
     * @return resultat de la petició
     */
    private TestResponse buidaCampionat() {
        return peticio("GET", App.LLUITADORS_RING_BUIDA, null);
    }

    private TestResponse lluita(String lluitador1, String lluitador2) {
        formParams.add(new BasicNameValuePair("nom1", lluitador1));
        formParams.add(new BasicNameValuePair("nom2", lluitador2));
        TestResponse res = peticio("POST", App.LLUITADORS_RING_LLUITA, formParams);
        if (res == null) {
            fail(LA_RESPOSTA_NO_HAURIA_DE_SER_NULL);
        }
        assertEquals(302, res.status);
        assertEquals(SERVER_URL + App.LLUITADORS_RING_RESULTAT, res.getRedirects());

        res = peticio("GET", App.LLUITADORS_RING_RESULTAT, null);
        return res;
    }

    /**
     * Resultat d'una petició GET o POST
     * @param metode Mètode a usar
     * @param path   URL Relativa
     * @param postParams Paràmetres en POST
     * @return resultat de la petició
     */
    private TestResponse peticio(String metode, String path, List<NameValuePair> postParams) {

        HttpResponse response = null;
        try {
            switch (metode) {
                case "GET":
                    HttpGet que = new HttpGet(SERVER_URL + path);
                    response = client.execute(que);
                    break;
                case "POST":
                    HttpPost envia = new HttpPost(SERVER_URL + path);
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams, "UTF-8");
                    envia.setEntity(entity);
                    response = client.execute(envia);
                    break;
            }

            int code = response.getStatusLine().getStatusCode();
            String body = IOUtils.toString(response.getEntity().getContent());

            TestResponse resposta = new TestResponse(code, body);

            if (code/100 == 3) {
                Header[] headers = response.getHeaders("Location");
                resposta.setRedirects(headers[0].getValue());
            }

            return resposta;

        } catch (IOException e) {
            e.printStackTrace();
            fail("Sending request failed: " + e.getMessage());
            return null;
        }
    }

//    /**
//     * Genera una resposta a partir del mètode i la ruta
//     *
//     * @param method Mètode
//     * @param path   Ruta
//     * @return Retorna un objecte TestResponse
//     */
//    private TestResponse request(String method, String path, String params) {
//        try {
//            URL url = new URL(SERVER_URL + path);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod(method);
//            connection.setInstanceFollowRedirects(false);
//            connection.setDoOutput(true);
//            connection.connect();
//
//            String body = IOUtils.toString(connection.getInputStream());
//            return new TestResponse(connection.getResponseCode(), body);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            fail("Sending request failed: " + e.getMessage());
//            return null;
//        }
//    }

    private static class TestResponse {

        final String body;
        final int status;
        String redirects;

        TestResponse(int status, String body) {
            this.status = status;
            this.body = body;
            this.redirects = null;
        }

        String content() {
            return body;
        }

        void setRedirects(String x) {
            this.redirects = x;
        }

        String getRedirects() {
            return redirects;
        }
    }
}
