/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.client.test.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.openscada.da.client.base.browser.HiveConnection;
import org.openscada.da.client.base.browser.HiveConnectionInformation;
import org.openscada.da.client.test.Activator;

public class NewHiveWizard extends Wizard implements INewWizard
{

    private NewHiveWizardConnectionPage page = null;

    @Override
    public boolean performFinish ()
    {
        final String connectionString = this.page.getConnectionString ();

        final IRunnableWithProgress op = new IRunnableWithProgress () {
            public void run ( final IProgressMonitor monitor ) throws InvocationTargetException
            {
                try
                {
                    doFinish ( monitor, connectionString );
                }
                catch ( final Exception e )
                {
                    throw new InvocationTargetException ( e );
                }
                finally
                {
                    monitor.done ();
                }
            }
        };
        try
        {
            getContainer ().run ( true, false, op );
        }
        catch ( final InterruptedException e )
        {
            return false;
        }
        catch ( final InvocationTargetException e )
        {
            final Throwable realException = e.getTargetException ();
            ErrorDialog.openError ( getShell (), Messages.getString ( "NewHiveWizard.errorDialog.title" ), realException.getMessage (), new Status ( Status.ERROR, Activator.PLUGIN_ID, realException.getMessage (), realException ) );
            realException.printStackTrace ();
            return false;
        }
        return true;
    }

    private void doFinish ( final IProgressMonitor monitor, final String connectionString ) throws Exception
    {

        monitor.beginTask ( Messages.getString ( "NewHiveWizard.finishJob.title" ), 2 ); //$NON-NLS-1$

        // add the hive
        final HiveConnectionInformation info = new HiveConnectionInformation ( connectionString );

        final HiveConnection connection = new HiveConnection ( info );
        Activator.getRepository ().addConnection ( connection );
        monitor.worked ( 1 );

        // store all
        monitor.subTask ( Messages.getString ( "NewHiveWizard.finishJob.subJobSave.title" ) ); //$NON-NLS-1$
        Activator.getRepository ().save ( Activator.getRepostoryFile () );
        monitor.worked ( 1 );
    }

    public void init ( final IWorkbench workbench, final IStructuredSelection selection )
    {
        setNeedsProgressMonitor ( true );
        setDefaultPageImageDescriptor ( Activator.getImageDescriptor ( "icons/48x48/stock_channel.png" ) ); //$NON-NLS-1$
    }

    @Override
    public void addPages ()
    {
        super.addPages ();

        addPage ( this.page = new NewHiveWizardConnectionPage () );
    }

}