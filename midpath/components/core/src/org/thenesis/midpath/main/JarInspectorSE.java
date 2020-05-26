/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 */
package org.thenesis.midpath.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;

public class JarInspectorSE extends AbstractJarInspector {

	private File file;
	private URL url;
	private URLClassLoader classLoader;

	public JarInspectorSE(File file) throws IOException {
		this.file = file;
		try {
			this.url = file.toURL();
			//this.url= new URI(file.getPath()).toURL();
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	public URLClassLoader getURLClassLoader() {
		if (classLoader == null) {
			classLoader = new URLClassLoader(new URL[] { url });
		}
		return classLoader;
	}

	public InputStream getManifest() throws IOException {
		URL u = new URL("jar", "", url + "!/META-INF/MANIFEST.MF");
		JarURLConnection uc = (JarURLConnection) u.openConnection();
		return uc.getInputStream();
	}

	public File getFile() {
		return file;
	}

	public URL getUrl() {
		return url;
	}

	//	/*public String getMainClassName() throws IOException {
	//		URL u = new URL("jar", "", url + "!/");
	//		JarURLConnection uc = (JarURLConnection) u.openConnection();
	//		Attributes attr = uc.getMainAttributes();
	//		return attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
	//	}
	//	
	//	public String listMidlets() throws IOException {
	//		URL u = new URL("jar", "", url + "!/");
	//		JarURLConnection uc = (JarURLConnection) u.openConnection();
	//		Attributes attr = uc.getMainAttributes();
	//		
	//		Set entrySet = (Set)attr.entrySet();
	//		if (entrySet != null) {
	//			Iterator iterator = entrySet.iterator();
	//			while(iterator.hasNext()) {
	//				System.out.println(iterator.next());
	//			}
	//		}
	////		for  (int i = 0; i < entries.size(); i++) {
	////			entries.attr.size());
	////		}
	//		return null;
	//		//return attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
	//	}
	//
	//	public void invokeClass(String name, String[] args) throws ClassNotFoundException, NoSuchMethodException,
	//			InvocationTargetException {
	//		Class c = loadClass(name);
	//		Method m = c.getMethod("main", new Class[] { args.getClass() });
	//		m.setAccessible(true);
	//		int mods = m.getModifiers();
	//		if (m.getReturnType() != void.class || !Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
	//			throw new NoSuchMethodException("main");
	//		}
	//		try {
	//			m.invoke(null, new Object[] { args });
	//		} catch (IllegalAccessException e) {
	//			// This should not happen, as we have 
	//			// disabled access checks
	//		}
	//	}

}
