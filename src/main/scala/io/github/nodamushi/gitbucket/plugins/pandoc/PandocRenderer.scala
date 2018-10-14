package io.github.nodamushi.gitbucket.plugins.pandoc

import java.io.{BufferedReader, InputStreamReader}

import gitbucket.core.plugin.{RenderRequest, Renderer}
import play.twirl.api.Html

import scala.io.Source
import scala.util.matching.Regex


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
  val regex = """<img +src *= *"([^">]+)" */?>""".r
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
        val s = Iterator.continually(i.readLine()).takeWhile(_ != null).mkString("\n")
        val m = regex.findAllIn(s)
        println(m)
        if(m.isEmpty){
          s
        }else{
          val sb = new StringBuilder
          var i = 0
          m.matchData.filter(m=>{
            val url = m.group(1)
            !(url.startsWith("http://") || url.startsWith("https://") ||
              url.startsWith("ftp://") ||url.startsWith("file://"))
          }).foreach(m=>{
            val ed  = m.end(1)
            sb.append(s.substring(i,ed)).append("?raw=true")
            i = ed
            println(i)
          })
          if(i == 0)  s
          else {
            sb.append(s.substring(i))
            sb.toString()
          }
        }
      }finally {
        process.destroy()
      }
    }catch{
      case e  => {e.printStackTrace();  s"Pandoc:${e.getClass.getName} Error =${e.getMessage}<br><pre>${content}</pre>"}
    }
  }

}
