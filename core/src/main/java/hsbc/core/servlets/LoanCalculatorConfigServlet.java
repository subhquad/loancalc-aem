package hsbc.core.servlets;

import com.day.cq.commons.jcr.JcrUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.util.NodeUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.*;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;


@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Simple Demo Servlet",
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.paths=/bin/servlet/loancalculatorconfig"
        })
public class LoanCalculatorConfigServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUid = 1L;

    @Override
    protected void doGet(final SlingHttpServletRequest request,
                         final SlingHttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("text/plain");
            ResourceResolver resourceResolver = request.getResourceResolver();
            Resource pageResource = resourceResolver.resolve(request.getParameter("pagePath") + "/jcr:content");
            Iterator<Resource> children = pageResource.getChildren().iterator();
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray=new JSONArray();
            while (children.hasNext()) {
                JSONObject jObject = new JSONObject();
                Node node = children.next().adaptTo(Node.class);
                jObject.put("configName", node.getName());
                jObject.put("property_showCurrencyInput", node.hasProperty("property_showCurrencyInput") ? node.getProperty("property_showCurrencyInput").getBoolean() : false);
                jObject.put("files", "[]");
                jsonArray.put(jObject);
            }
            jsonObject.put("data",jsonArray);
            resp.getWriter().write(jsonObject.toString());
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void doPost(final SlingHttpServletRequest request,
                          final SlingHttpServletResponse response) throws ServletException, IOException {

        try {
            final String action = request.getParameter("action");
            PrintWriter writer = response.getWriter();

            ResourceResolver resourceResolver = request.getResourceResolver();

            if (StringUtils.isNotEmpty(action)) {
                Resource pageResource = resourceResolver.resolve(request.getParameter("pagePath") + "/jcr:content");

                if ("create".equals(action)) {

                    Node pageNode = pageResource.adaptTo(Node.class);
                    Node configNode = null;

                    String configName = request.getParameter("configName");
                    String validNodeName = JcrUtil.createValidName(configName);
                    if (pageNode.hasNode(validNodeName)) {
                        configNode = pageNode.getNode(validNodeName);
                    } else {
                        configNode = pageNode.addNode(validNodeName);
                    }
                    configNode.setProperty("configTitle", configName);

                    Map parameterMap = request.getParameterMap();

                    for (Object key : parameterMap.keySet()) {
                        String keyStr = (String) key;
                        if (keyStr.startsWith("property")) {
                            Boolean value = Boolean.parseBoolean(request.getParameter(keyStr));
                            configNode.setProperty(keyStr, value);
                            resourceResolver.commit();
                        }

                    }

                }

                writer.write("{\"data\":[],\"files\":{\"files\":{\"1\":{\"id\":\"1\",\"filename\":\"image (1).jpg\",\"filesize\":\"74206\",\"web_path\":\"\\/upload\\/1.jpg\",\"system_path\":\"\\/home\\/datat\\/public_html\\/editor\\/upload\\/1.jpg\"}}},\"upload\":{\"id\":\"1\"}}");
            } else if ("upload".equals(action)) {
                final boolean isMultipart = org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent(request);
                PrintWriter out = null;

                out = response.getWriter();
                if (isMultipart) {
                    final java.util.Map<String, org.apache.sling.api.request.RequestParameter[]> params = request.getRequestParameterMap();
                    //for (final java.util.Map.Entry<String, org.apache.sling.api.request.RequestParameter[]> pairs : params.entrySet()) {
                    //final String k = pairs.getKey();

                    final org.apache.sling.api.request.RequestParameter[] pArr = params.get("csvFileUpload");//.getValue();
                    final org.apache.sling.api.request.RequestParameter param = pArr[0];
                    final InputStream stream = param.getInputStream();
                    String fileContent=IOUtils.toString(stream);

                    //}
                }
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
    //Save the uploaded file into the AEM DAM using AssetManager APIs
        private String writeToDam(InputStream is, String fileName,ResourceResolver  resourceResolver)
        {
            try
            {

                //Use AssetManager to place the file into the AEM DAM
                com.day.cq.dam.api.AssetManager assetMgr = resourceResolver.adaptTo(com.day.cq.dam.api.AssetManager.class);
                String newFile = "/content/dam/travel/"+fileName ;
                assetMgr.createAsset(newFile, is,"image/jpeg", true);

                // Return the path to the file was stored
                return newFile;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

}
