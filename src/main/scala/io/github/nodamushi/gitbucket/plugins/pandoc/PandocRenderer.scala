package io.github.nodamushi.gitbucket.plugins.pandoc

import java.io.{BufferedReader, InputStreamReader}

import gitbucket.core.plugin.{RenderRequest, Renderer}
import play.twirl.api.Html

import scala.io.Source


class PandocRenderer extends Renderer{
  override def render(request: RenderRequest): Html = {
    Html(s"""<script type="text/javascript">(function(){
            |  var link = document.createElement('link');
            |  link.href = '${request.context.path}/plugin-assets/org_pandoc/style.css';
            |  link.rel = 'stylesheet';link.type = 'text/css';
            |  var head = document.getElementsByTagName('head')[0];
            |  head.appendChild(link);
            |})();</script>""".stripMargin +
      org2html(request.fileContent))
  }

  def org2html(content:String): String ={
    try {
      val builder = new ProcessBuilder("pandoc", "-f", "org", "-t", "html5")
      builder.redirectErrorStream(true)
      val process = builder.start
      try {
        val o = process.getOutputStream
        try o.write(content.getBytes("utf-8"))
        finally o.close()
        val i = new BufferedReader(new InputStreamReader(process.getInputStream,"utf-8"))
        Iterator.continually(i.readLine()).takeWhile(_ != null).mkString("\n")
      }finally {
        process.destroy()
      }
    }catch{
      case e  => {e.printStackTrace();  s"Pandoc:${e.getClass.getName} Error =${e.getMessage}<br><pre>${content}</pre>"}
    }
  }

}
