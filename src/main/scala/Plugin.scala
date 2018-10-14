import gitbucket.core.plugin.Renderer
import io.github.gitbucket.solidbase.model.Version
import io.github.nodamushi.gitbucket.plugins.pandoc.PandocRenderer

class Plugin extends gitbucket.core.plugin.Plugin {
  override val pluginId: String = "org_pandoc"
  override val pluginName: String = "org-mode Render Plugin with Pandoc"
  override val description: String = "Renders org-mode with pandoc"
  override val versions: List[Version] = List(
    new Version("1.0.0")
  )
  override val assetsMappings: Seq[(String, String)] = Seq("/org_pandoc"->"/org_pandoc")
  override val renderers: Seq[(String, Renderer)] = Seq("org"->new PandocRenderer)
}
