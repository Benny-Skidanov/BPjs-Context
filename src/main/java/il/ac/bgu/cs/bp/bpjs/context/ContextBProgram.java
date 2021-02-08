package il.ac.bgu.cs.bp.bpjs.context;

import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import org.mozilla.javascript.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static java.util.stream.Collectors.joining;

public class ContextBProgram extends BProgram {
  private Collection<String> resourceNames;

  public ContextBProgram(String aResourceName) {
    this(Collections.singletonList(aResourceName), aResourceName);
  }

  public ContextBProgram(Collection<String> someResourceNames) {
    this(someResourceNames, someResourceNames.stream().collect(joining("+")));
  }

  public ContextBProgram(Collection<String> someResourceNames, String aName) {
    super(aName, new CtxEventSelectionStrategy());
    this.setStorageModificationStrategy(new ContextStorageModificationStrategy());
    putInGlobalScope("ctx_proxy", ContextProxy.Create(this));
    resourceNames = someResourceNames;
    resourceNames.forEach(this::verifyResourceExists);
  }

  @Override
  protected void setupProgramScope(Scriptable scope) {
    evaluateFile("context.js");
    resourceNames.forEach(this::evaluateFile);
    resourceNames = null; // free memory
  }

  private void evaluateFile(String name) {
    try (InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(name)) {
      if (resource == null) {
        throw new RuntimeException("Resource '" + name + "' not found.");
      }
      evaluate(resource, name);
    } catch (IOException ex) {
      throw new RuntimeException("Error reading resource: '" + name + "': " + ex.getMessage(), ex);
    }
  }

  private void verifyResourceExists( String resName ) {
    URL resUrl = Thread.currentThread().getContextClassLoader().getResource(resName);

    if ( resUrl == null ) {
      throw new IllegalArgumentException( "Cannot find resource '" + resName + "'");
    }
  }
}
